package app.shiva.ajna.model;

public class Consumer {
    private String consumerName;
    private String consumerEmail;
    private String photoUri;

    public Consumer() {
    }

    public Consumer(String consumerName, String consumerEmail,String photoUri) {
        this.consumerName = consumerName;
        this.consumerEmail = consumerEmail;
        this.photoUri=photoUri;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getConsumerEmail() {
        return consumerEmail;
    }

    public void setConsumerEmail(String consumerEmail) {
        this.consumerEmail = consumerEmail;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
