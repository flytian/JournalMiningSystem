package dataAnalysisBeans;

import JDBCUtils.JDBCUtils;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 机构分析 1、机构发文排名 2、机构被引排名
 *
 * @author Think
 */
@Named(value = "affiliationAnalysis")
@SessionScoped
public class AffiliationAnalysis implements Serializable {

    private ResultSet aff_publish;                      //机构发文量，格式（单位，发文总量）
    private ResultSet aff_ref;                          //机构被引次数， 格式（单位，总被引，篇均被引）
    private List<Map> affRefData;
    private GsonOption option;

    public AffiliationAnalysis() {
        getData();
    }

    public void getData() {
        Connection conn = null;
        Statement stat = null;
        ResultSet local = null;
        String sql = null;
        option = new GsonOption();
        CategoryAxis xAxis = new CategoryAxis();
        Bar bar = new Bar();
        Bar bar1 = new Bar();
        Line line = new Line();
        try {
            conn = JDBCUtils.getConn();
            stat = conn.createStatement();
            /*--start 单位发文统计 --*/
            sql = "select j_address , count(*) as 发文量 "
                    + " from journal_info,paper_address "
                    + " where journal_info.j_number = paper_address.j_number "
                    + " group by j_address "
                    + " order by 发文量 DESC ";
            aff_publish = stat.executeQuery(sql);

            while (aff_publish.next()) {
                xAxis.data(aff_publish.getString(1));
                bar.data(aff_publish.getInt(2));
            }

            /*--start 单位被引统计 --*/
            sql = "select j_address , sum(journal_info.j_citation_frequency) as refTimes,avg(journal_info.j_citation_frequency) as avgTimes "
                    + " from journal_info,paper_address "
                    + " where journal_info.j_number = paper_address.j_number "
                    + " group by j_address "
                    + " order by refTimes DESC,avgTimes DESC ";
            aff_ref = stat.executeQuery(sql);
            affRefData = new ArrayList();
            affRefData = JDBCUtils.getResultList(aff_ref);
            if (aff_ref.first()) {
                do {
//                    xAxis.data(if (aff_ref.next()) {aff_publish.getString(1));
                    bar1.data(aff_ref.getInt(2));//被引次数
                    line.data(aff_ref.getDouble(3));//篇均被引次数
                } while (aff_ref.next());
            }

        } catch (Exception e) {

        } finally {
            JDBCUtils.close(local, stat, conn);
        }
        bar1.name("被引次数");
        line.name("篇均被引次数");
        bar.name("发文量");
        option.xAxis(xAxis);
        option.series(bar);
        option.series(line);
        option.series(bar1);
        // System.out.println(option.toString());
        
    }
    
    public String getOption(){
        return option.toString();
    }

    public List<Map> getAffRefData() {
        return affRefData;
    }
    
}
