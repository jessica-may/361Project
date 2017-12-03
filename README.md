
 # 361Project: Geographical Recon Energy Grub Guide
 
 This repository holds the project that was created to meet the requirements of CSCE361's project. The Geographical Recon
 Energy Grub Guide (hereafter refered to as G.R.E.G.G) is a Android mobile application that displays a map centered on the
 University of Nebraska-Lincoln's City Campus and allows the student and faculty users to drop pins alerting other users of 
 food and free items on campus.
 
 # The Contents of this Repository
 
 The Design folder contains the design document for this project.
 The MyApplication folder contains the code written for this project.
 The Testing folder holds the testing documents.
 The Requirements folder hold the requirements specification written for the project.
 
 # Clarification on the Structure of the Project
 
 The application begins in the class LoginActivity, which uses activity_login.xml for the user interface (UI) aspect. It allows 
 the user to enter their login information or, if they do not have an account, to click the "Register Here" button and be taken 
 to create one. If the user clicks on this button the LoginActivity calls the RegisterActivity class, which uses 
 activity_register.xml for its UI. If the user creates a new account, the username and password they enter will be saved to the
 "user" database. When they need to log in, the LoginActivity uses the JDBCInterface class to find the username in the database 
 and check to see if the password entered on the login screen matches the one saved in the database.
 
 Once logged in, the application loads the map, which uses the Google Map API. This map will initially be centered on the Union
 and the user can move the screen to look at different areas on campus. LoginActivity does this by calling the MapsActivity 
 class when the login button is pressed and passes (the correct login information was entered). When the map loads, MapsActivity
 uses the JDBCInterface class to see which locations have had pins dropped on them already and if three or more users have 
 dropped a pin at that location, a red marker appears on the map.
 
 On this map screen, there are two buttons at the bottom ("Make Pin" and "Account"). If one clicks on the "Account" button, the
 screen will change to a new screen, MapsActivity will call AccountActivity, and display options the user can make relating to 
 their account. They will be able to change their password, delete their account, or logout.
 
 If the user clicks on the "Make Pin" button, the screen will now let them choose between which type of pin they would like to 
 create, food or free items. This button causes MapsActivity to switch to the CreatePin class, activity_makepins.xml for the 
 UI. 
 After the user chooses the type of pin, the application goes to a form screen where they can specify the location where the 
 event is at and leave a comment to let other users know more about the event. Once the user submits the pin creation form,the 
 application returns to the map screen and if they were the third person to add a pin to that location, a marker will be on 
 that building.
 
 When on the map screen, if a user clicks on a marker that is present, the MapsActivity class will activate the PinDisplay 
 class, activity_pin_display.xml for the UI. On this screen, the type of pin and location will be displayed e.g. "Food at 
 Avery". Below this title, there will be a vote count displayed that shows if other users backed this event up by upvoting it, 
 or downvoted to say that it wasn't going on anymore. If there are 3 downvotes, the pin will be removed. Below the vote count, 
 all the comments entered during the pin creation form will be displayed. Below this the upvote and downvote buttons are 
 available as well as a report button. This report button allows users to report an event if there are harmful comments taking
 place or the event is not real.

 # Database Component
 The application uses a MySQL database to store users, buildings, pins, votes, and reports. All database interactions are handled by the
 JDBCInterface class. The Connector/J jarfile available on the MySQL website along with java.sql packages are used to communicate
 with the database. Because the Android operating system does not allow internet access on an application's main thread, database
 actions are run in ExecuteQueueTasks, which extends android.os.AsyncTask. Queued database tasks are stored in a java.util.Vector
 of DBRequest objects. Each of these objects stores the text of a MySQL statement, a boolean representing whether the statement is
 an update or a query, and a ResultSetHolder to allow query results to be accessed from the main thread. The method
 addRequestToQueue is used to add new requests to the queue. This method first checks whether the queue is empty. The new request
 is then added to the queue, and if the queue was empty when checked then a new instance of ExecuteQueueTasks is created. Update
 requests can be added only need to be added to the queue, but query requests require a little more. After the query request is
 added, queueTasks.get() is used to block the main thread until the query is complete and the ResultSet is then acquired from the
 ResultSetHolder. 
