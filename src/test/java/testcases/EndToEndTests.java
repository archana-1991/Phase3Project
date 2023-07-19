package testcases;

import java.util.List;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndToEndTests {

    static String baseUri = "http://localhost:8088/employees/";
    
	@Test
	public void EndToEndTest() {

		// get all employees
		Response response = Getallemployees();
		String body = response.getBody().asString();
		System.out.println("Retrieved all the members " + body);

		JsonPath json = response.jsonPath();
		List<Integer> ids = json.get("id");
		Assert.assertEquals(200, response.statusCode());
		Assert.assertEquals(3, ids.size());
		
		System.out.println("The all Employees are " + ids.size());
        System.out.println("the status code is " + response.statusCode());

		// create new employee
		Response createResponse = CreateNewEmployee("Jhon", "Peddy", 1000, "test@test.com");
		JsonPath createdjson = createResponse.jsonPath();
		Integer createdId = createdjson.get("id");

		Assert.assertEquals(201, createResponse.getStatusCode());
		body = createResponse.getBody().asString();
		System.out.println("Created new employee with an Id " + createdId + " with details " + body);

		Response allEmployeesResponse = Getallemployees();
		JsonPath createjson = allEmployeesResponse.jsonPath();
		ids = createjson.get("id");
		Assert.assertEquals(4, ids.size());

		// edit an employee
		Response editResponse = EditEmployee(createdId, "Tushar", "Gupta", 2000, "test2@test.com");
		Assert.assertEquals(200, editResponse.getStatusCode());

		body = editResponse.getBody().asString();
		System.out.println("Edited an employee with an Id " + createdId + " with details: " + body);
		
		Response getResponse = GetEmployee(createdId);
		Assert.assertEquals(200, getResponse.getStatusCode());
		JsonPath editjson = getResponse.jsonPath();
		String fName = editjson.get("firstName");
		String lName = editjson.get("lastName");
		int salary = editjson.get("salary");

		Assert.assertEquals(fName, "Tushar");
		Assert.assertEquals(lName, "Gupta");
		Assert.assertEquals(salary, 2000);
		
		System.out.println("The updated employees are :");

		// delete an employee that created above
		Response deleteResponse = DeleteEmplyee(createdId);
		Assert.assertEquals(200, deleteResponse.getStatusCode());

		System.out.println("Deleted an employee with an Id " + createdId);
		
		
		Response getResponse2 = GetEmployee(createdId);
		Assert.assertEquals(400, getResponse2.getStatusCode());
		
		System.out.println("The Response code is" + getResponse2.getStatusCode());

		// make sure we are at same stage as we were at initial stage
		Response responseAll = Getallemployees();
		JsonPath jsonAll = responseAll.jsonPath();
		List<Integer> idsAll = jsonAll.get("id");

		body = responseAll.getBody().asString();
		System.out.println("Retrieved all the members " + body);
		Assert.assertEquals(200, responseAll.statusCode());
		Assert.assertEquals(3, idsAll.size());
		
		System.out.println("End of the employees are :" +idsAll.size() );
	}

	public Response Getallemployees() {

		RestAssured.baseURI =baseUri;
		RequestSpecification request = RestAssured.given();
		Response response = request.get();

		return response;
	}

	public Response CreateNewEmployee(String firstName, String lastName, int salary, String email) {

		JSONObject requestBody = new JSONObject();
		requestBody.put("firstName", firstName);
		requestBody.put("lastName", lastName);
		requestBody.put("salary", salary);
		requestBody.put("email", email);

		RestAssured.baseURI = baseUri;
		RequestSpecification request = RestAssured.given();
		Response response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody.toString())
				.post();

		return response;
	}

	public Response EditEmployee(int id, String firstName, String lastName, int salary, String email) {
		JSONObject requestBody = new JSONObject();
		requestBody.put("firstName", firstName);
		requestBody.put("lastName", lastName);
		requestBody.put("salary", salary);
		requestBody.put("email", email);

		RestAssured.baseURI = baseUri + id;
		RequestSpecification request = RestAssured.given();
		Response response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody.toString())
				.put();

		return response;
	}

	public Response GetEmployee(int id) {
		RestAssured.baseURI = baseUri + id;
		RequestSpecification request = RestAssured.given();
		Response response = request.get();

		return response;
	}

	public Response DeleteEmplyee(int id) {
		RestAssured.baseURI = baseUri + id;
		RequestSpecification request = RestAssured.given();
		Response response = request.delete();

		return response;
	}
}
