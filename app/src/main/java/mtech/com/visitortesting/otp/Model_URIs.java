package mtech.com.visitortesting.otp;

public class Model_URIs {

	String TransactionLog_URI;
	String Perform_URI;

	public Model_URIs(){

		//this.TransactionLog_URI = "http://ekyc.m-techinnovations.com/aadhaarAuthenticationAPI/AadhaarAuthenticationService.svc/GenerateOTPForAadhaarAuthenticationService";
//		this.TransactionLog_URI = "http://ekyc.m-techinnovations.com/aadhaarAuthenticationAPI/AadhaarAuthenticationService.svc/GenerateOTPAuthenticationService";
//
//		this.Perform_URI = "http://ekyc.m-techinnovations.com/aadhaarAuthenticationAPI/AadhaarAuthenticationService.svc/OTPAuthenticationService";

//		this.TransactionLog_URI = "http://ekyc.m-techinnovations.com/aadhaarAuthenticationAPI/AadhaarAuthenticationService.svc/GenerateOTPForAadhaarAuthenticationService";
//		this.Perform_URI = "http://ekyc.m-techinnovations.com/aadhaarAuthenticationAPI/AadhaarAuthenticationService.svc/PerformOTPAadhaarAuthenticationService";

		this.TransactionLog_URI = "http://ekyc.m-techinnovations.com/aadhaarAuthenticationAPI/AadhaarAuthenticationService.svc/GenerateOTPAndroidService";
		this.Perform_URI = "http://services.m-techinnovations.com/SMSSendService/SMSSendService.svc/SendSMSService";
//		this.Perform_URI="http://www.smsgateway.center/SMSApi/rest/send?userId=diginexus&password=Admin@1234$&senderId=MTECHI&sendMethod=simpleMsg&msgType=TEXT&msg=good%20After%20noon%20&mobile=917020167785&duplicateCheck=true&format=json";
	}
	public String getTransactionLog_URI(){
		return this.TransactionLog_URI;
	}
	public String getPerform_URI() {
		return this.Perform_URI;
	}
}
