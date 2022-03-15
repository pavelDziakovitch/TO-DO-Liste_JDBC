public class User {

    int userID;
    String email;
    String password;
    String l_name;
    String f_name;


    public User(int id, String email, String password, String l_name, String f_name) {
        if(id != 0){
            this.userID = id;
        }
        if(email != null){
            this.email = email;
        }
        if(password != null){
            this.password = password;
        }
        if(l_name != null){
            this.l_name = l_name;
        }
        if(f_name != null){
            this.f_name = f_name;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", l_name='" + l_name + '\'' +
                ", f_name='" + f_name + '\'' +
                '}';
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }
}
