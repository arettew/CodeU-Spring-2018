package codeu.controller;

final class ServletUrlStrings {

    static String leadershipJsp;
    static String adminviewJsp;
    static String registerJsp;
    static String loginJsp;

    private void ServletUrlString() {
    	this.leadershipJsp = "/WEB-INF/view/leaderboard.jsp";
    	this.adminviewJsp = "/WEB-INF/view/adminview.jsp";
    	this.registerJsp = "/WEB-INF/view/register.jsp";
    	this.loginJsp = "/WEB-INF/view/login.jsp";
    }

    public String getLeadershipJsp() {
    	return this.leadershipJsp;
    }

    public String getAdminviewJsp() {
    	return this.adminviewJsp;
    }
    // changing strings in registerservlet and loginservlet using this file
    // causes tests to fail? 
    public String getregisterJsp() {
    	return this.registerJsp;
    }

    public String getloginJsp() {
    	return this.loginJsp;
    }
}