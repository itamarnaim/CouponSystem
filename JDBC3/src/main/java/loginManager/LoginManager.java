package loginManager;

import facade.ClientFacade;
import facade.CompanyFacade;
import facade.CustomerFacade;
import facade.AdminFacade;


public class LoginManager {

    private static LoginManager instance = null;

    private LoginManager (){}

    public static LoginManager getInstance(){
        if (instance == null){
            instance = new LoginManager();
        }
        return instance;
    }

    public ClientFacade login (String email , String password, clientType clientType){
        switch (clientType){

            case COMPANY:
                CompanyFacade companyFacade = new CompanyFacade();
                return companyFacade.login(email,password) ? companyFacade : null;

            case CUSTOMER:
                CustomerFacade customerFacade = new CustomerFacade();
                return customerFacade.login(email,password) ? customerFacade : null;

            case ADMINISTRATOR:
                AdminFacade adminFacade = new AdminFacade();
                return adminFacade.login(email,password) ? adminFacade : null;

        }
        return null;
    }
}
