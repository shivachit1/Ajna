package app.shiva.ajna.model;

public class Issue {
    String issueTitle;
    String issueDescription;

    public Issue(String issueTitle, String issueDescription) {
        this.issueTitle = issueTitle;
        this.issueDescription = issueDescription;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }
}
