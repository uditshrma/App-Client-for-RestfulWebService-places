package tk.uditsharma.clientapp.model;


public class AllPlacesResponse {

    private String date;
    private String placeId;
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    public AllPlacesResponse(String date, String placeId) {
        this.date = date;
        this.placeId = placeId;
    }

}
