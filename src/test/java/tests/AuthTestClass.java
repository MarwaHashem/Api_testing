package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.logs.Log;

import java.lang.reflect.Method;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utils.extentreports.ExtentTestManager.startTest;

public class AuthTestClass{

        String loginToken=null;
        String bookingId=null;

        @BeforeClass
        public void login (){
           Log.info("Tests is starting!");
            String endPointUrl="https://restful-booker.herokuapp.com/auth";
            String body= """
        {
            "username" : "admin",
            "password" : "password123"
        }
         """;
            ValidatableResponse validatableResponse=given().body(body)
                    .header("Content-Type","application/json")
                    .when().post(endPointUrl).then();

            Response response= validatableResponse.extract().response();
            JsonPath jsonPath=response.jsonPath();
            loginToken=jsonPath.getString("token");
           // bookingId=jsonPath.getString("booking.bookingid");
        }
        @Test(priority = 0)
        public void testCreateBooking(Method method){
            startTest(method.getName(), "Invalid Login Scenario with empty username and password.");
           String endPointUrl="https://restful-booker.herokuapp.com/booking";
            String body= """
             {
                 "firstname" : "Jim",
                 "lastname" : "Brown",
                 "totalprice" : 111,
                 "depositpaid" : true,
                 "bookingdates" : {
                     "checkin" : "2018-01-01",
                     "checkout" : "2019-01-01"
                 },
                 "additionalneeds" : "Breakfast"
             }
              """;
            var   validatableResponse= given().body(body)
                    .header("Content-Type","application/json")

                    .when().post(endPointUrl).then();
            validatableResponse.body("booking.firstname",equalTo("Jim"));
            validatableResponse.body("booking.lastname",equalTo("Brown"));
            validatableResponse.body("booking.totalprice",equalTo( 111 ));

            Response response= validatableResponse.extract().response();
            JsonPath jsonPath=response.jsonPath();
            bookingId=jsonPath.getString("bookingid");
        }
        @Test(priority = 1, dependsOnMethods = "testCreateBooking")
        public void testEditBooking(Method method){
            startTest(method.getName(), "Invalid Login Scenario with empty username and password.");

            String endPointUrl="https://restful-booker.herokuapp.com/booking/" +bookingId;
            String body= """
                {
                    "firstname" : "James",
                    "lastname" : "Brown",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2018-01-01",
                        "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }
                 """;
            var   validatableResponse= given().body(body)
                    .header("Content-Type","application/json")
                    .header("Cookie","token=" + loginToken)
                    .header("Authorisation","Basic")
                    .when().put(endPointUrl).then();

            validatableResponse.body("firstname",equalTo("James"));
            validatableResponse.body("lastname",equalTo("Brown"));
            validatableResponse.body("totalprice",equalTo( 111 ));
        }

        @Test(priority = 2, dependsOnMethods = "testEditBooking")
        public void testGetBooking(Method method){
            startTest(method.getName(), "Invalid Login Scenario with empty username and password.");
            String endPointUrl="https://restful-booker.herokuapp.com/booking/" +bookingId;
            var validatableResponse = given()
                    .header("Accept","application/json")
                    .when().get(endPointUrl).then();
            validatableResponse.body("firstname",equalTo("James"));
            validatableResponse.body("lastname",equalTo("Brown"));
            validatableResponse.body("totalprice",equalTo( 111 ));
        }

        @Test(priority = 3, dependsOnMethods = "testGetBooking")
        public void testDeleteBooking(Method method){
            startTest(method.getName(), "Invalid Login Scenario with empty username and password.");
            String endPointUrl="https://restful-booker.herokuapp.com/booking/" +bookingId;

            var validatableResponse = given()
                    .header("Content-Type" , "application/json")
                    .header("Cookie","token="+ loginToken)
                    .header("Authorisation","Basic")
                    .when().delete(endPointUrl).then();

            Response response= validatableResponse.extract().response();
            Assert.assertEquals(response.asString(),"Created");

        }


    }


