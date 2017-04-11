
package info;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;



public class PaperInfo {
    private String title;
    private String number;
    private String authors;
    private String affiliation;
    private String keyword;
    private String abstract1;
    private String fund;
    private String origin;
    private String year;
    private String pages;
    private String classNo;
    private String citation_frequency;
    private String othersCitation;
    private String[] authorList;
    private String[] keywordList;
    private String[] fundList;
    private String[] affiliationList;
   
    
   
    public PaperInfo() {
    }

    public void createJournalInfo(){
        
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }    
    public String[] getAuthorList() {
        setAuthorList();
        return authorList;
    }
    public void setAuthorList() {
        this.authorList = this.authors.split(",");
    }
    public String[] getKeywordList() {
        setKeywordList();
        return keywordList;
    }
    public void setKeywordList() {
        this.keywordList = this.keyword.split(",");
    }
    public String[] getFundList() {
        setFundList();
        return fundList;
    }
    public void setFundList() {
        if(this.fund!=null){
            this.fund = this.fund.replaceAll("\\([^\\)]+\\)", "");
            this.fundList = this.fund.split(",");
            Set temp = new HashSet();
            temp.addAll(Arrays.asList(this.fundList));
            Object[] object = temp.toArray();
            String[] str = new String[object.length];
            for (int i = 0; i < object.length; i++) {
                str[i] = object[i].toString();
            }
            this.fundList = str;
        }
        
    }
    public String[] getAffiliationList() {
        setAffiliationList();
        return affiliationList;
    }
    public void setAffiliationList() {
        this.affiliationList = this.affiliation.split(",");
    }
    public String getTitle() {
        
        return title;
    }
    public void setTitle(String title) {
        
        this.title = title.replaceAll("'", "\"");
    }
    public String getAuthors() {
        return authors;
    }
    public void setAuthors(String authors) {
        this.authors = authors.replaceAll("'", "\"");
    }
    public String getAffiliation() {
        return affiliation;
    }
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public String getAbstract1() {
        return abstract1;
    }
    public void setAbstract1(String abstract1) {
        this.abstract1 = abstract1;
    }
    public String getFund() {
        return fund;
    }
    public void setFund(String fund) {
        this.fund = fund;
    }
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year.substring(0,year.indexOf(","));
    }
    public String getPages() {
        return pages;
    }
    public void setPages(String pages) {
        this.pages = pages;
    }
    public String getClassNo() {
        return classNo;
    }
    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }
    public String getCitation_frequency() {
        return citation_frequency;
    }
    public void setCitation_frequency(String citation_frequency) {
        this.citation_frequency = citation_frequency;
    }
    public String getOthersCitation() {
        return othersCitation;
    }
    public void setOthersCitation(String othersCitation) {
        this.othersCitation = othersCitation;
    }
    
    @Override
    public String toString() {
        return "PaperInfo{" + "title=" + title + ", authors=" + authors + ", affiliation=" + affiliation + ", keyword=" + keyword + ", abstract1=" + abstract1 + ", fund=" + fund + ", origin=" + origin + ", year=" + year + ", pages=" + pages + ", classNo=" + classNo + ", citation_frequency=" + citation_frequency + ", othersCitation=" + othersCitation + '}';
    }
    
    public Statement addSQL(Statement stat) throws SQLException{
        String sql;
        sql = "insert into journal_info(j_number,j_title,j_abstract,j_citation_frequency,j_others_citation,j_pages,j_class_No,j_year) values('"+ number + "','"+ title + "','"+ abstract1 + "',"+ citation_frequency + "," + othersCitation +",'"+ pages +  "','" + classNo +  "','" + year +"')";
        stat.addBatch(sql);
        for(String item : getAuthorList()){
            sql = "insert into paper_author(j_number,j_author) values('"+ number + "','" + item + "')";
            stat.addBatch(sql);    
        }
        for(String item : getAffiliationList()){
            sql = "insert into paper_address(j_number,j_address) values('"+ number + "','" + item + "')";
            stat.addBatch(sql);    
        }
        if(fund!=null && !fund.equals("")){
            for(String item : getFundList()){
            sql = "insert into paper_fund(j_number,j_fund) values('"+ number + "','" + item + "')";
            stat.addBatch(sql);    
        }
        }
        for(String item : getKeywordList()){
            sql = "insert into paper_keywords(j_number,keyword) values('"+ number + "','" + item + "')";
            stat.addBatch(sql);    
        }
        return stat;
        
    }
    
    
}