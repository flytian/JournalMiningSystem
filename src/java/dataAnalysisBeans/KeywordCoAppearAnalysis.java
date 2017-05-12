package dataAnalysisBeans;

import JDBCUtils.JDBCUtils;
import com.github.abel533.echarts.series.Graph;
import com.github.abel533.echarts.series.force.Node;
import com.github.abel533.echarts.style.LineStyle;
import com.google.gson.Gson;
import info.GraphLink;
import info.GraphNormal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Named;
import javax.enterprise.context.Dependent;

@Named(value = "keywordCoAppearAnalysis")
@Dependent
public class KeywordCoAppearAnalysis {

    private Graph graph;
    private List<GraphLink> links;
    private List<Node> nodess;
    private Map<String,List> data;

    public KeywordCoAppearAnalysis() {
        setAllData();
    }

    public void setAllData() {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        String sql;
        graph = new Graph();
        links = new ArrayList<>();
        nodess = new ArrayList<>();
        data = new HashMap<>();
        try {
            conn = JDBCUtils.getConn();
            stat = conn.createStatement();
//            sql = "select distinct keyword,count(*) "
//                    + "from paper_keywords "
//                    + "group by keyword "
//                    + "having count(*)>1";
//            rs = stat.executeQuery(sql);
//            while (rs.next()) {
//                
//            }
            sql = "select distinct j_orgin from journal_info";
            rs = stat.executeQuery(sql);
            String journalName ="";
            if(rs.next()){
                journalName = rs.getString(1);
            }
            Set<String> nodes = new HashSet<>();
            sql = "select  a.keyword, b.keyword ,count(*) as 共现次数 "
                    + "from  paper_keywords a, paper_keywords b "
                    + "where a.j_number=b.j_number and a.keyword<b.keyword and a.keyword in (select distinct keyword from paper_keywords group by keyword having count(*)>1) "
                    + "group by a.keyword,b.keyword  "
                    + "having count(*)>1 "
                    + "order by count(*) desc";
            rs = stat.executeQuery(sql);
            int max =-1;
            while (rs.next()) {
                if(max==-1){
                    max = rs.getInt(3);
                }
                nodes.add(rs.getString(1));
                nodes.add(rs.getString(2));
                GraphLink link = new GraphLink();
                link.setSource(rs.getString(1));
                link.setTarget(rs.getString(2));
                link.setValue(rs.getInt(3));
                //link.setWeight(rs.getInt(3));
                LineStyle linestyle  = new LineStyle();
                GraphNormal normal = new GraphNormal();
                normal.setWidth((float) (rs.getInt(3)*5.0/max));
                linestyle.normal(normal);
                link.lineStyle(linestyle);
                links.add(link);
                graph.links(link);
            }
            for (String item : nodes) {
                Node node = new Node();
                node.setName(item);
                node.draggable(Boolean.TRUE);
                node.symbolSize(2);
                nodess.add(node);
                graph.data(node);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(rs, stat, conn);
        }

    }

    public String getGraph() {
        Gson gson = new Gson();
        System.out.println(gson.toJson(graph));
        return gson.toJson(graph);
    }
    public String getData(){
        data.put("links", links);
        data.put("nodes", nodess);
        Gson gson = new Gson();
        return gson.toJson(data);
    }

}