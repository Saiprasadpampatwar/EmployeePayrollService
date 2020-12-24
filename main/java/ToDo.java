public class ToDo {
    public int id;
    public String summary;
    public String desc;

    public ToDo(int id, String summary, String desc) {
        this.id = id;
        this.summary = summary;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
