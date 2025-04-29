# PPE Inventory Management System

A Java-based inventory management system for Personal Protective Equipment (PPE) with SQLite database integration.

## Prerequisites

- Java Development Kit (JDK) 17 or later
- SQLite JDBC Driver (included in the project)
- Visual Studio Code (recommended) or any Java IDE

## Installation

1. **Install Java Development Kit (JDK)**

   - Download and install JDK 17 or later from [Oracle's website](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK
   - Verify installation by running:
     ```bash
     java -version
     ```

2. **Install VS Code (Optional but Recommended)**

   - Download and install [Visual Studio Code](https://code.visualstudio.com/)
   - Install the "Extension Pack for Java" from the VS Code marketplace

3. **Clone or Download the Project**
   - Clone the repository or download the project files
   - Ensure all files are in the same directory

## Project Structure

- `LoginFrame.java` - Main entry point of the application
- `User.java` - User model class
- `DBConnection.java` - Database connection handler
- `DatabaseInitializer.java` - Database initialization
- `DashboardFrame.java` - Main dashboard after login
- `UserManagerGUI.java` - User management interface
- `SupplierHospitalFrame.java` - Supplier and hospital management
- `StockTrackingPanel.java` - Stock tracking interface
- `SearchPanel.java` - Search functionality
- `sqlite-jdbc-3.49.1.0.jar` - SQLite JDBC driver
- `ppe_inventory.db` - SQLite database file

## Running the Application

1. **Compile the Java Files**

   ```bash
   javac -cp ".:sqlite-jdbc-3.49.1.0.jar" *.java
   ```

2. **Run the Application**
   ```bash
   java -cp .:sqlite-jdbc-3.49.1.0.jar LoginFrame
   ```
   Note: On Windows, use semicolon (;) instead of colon (:) in the classpath:
   ```bash
   java -cp .;sqlite-jdbc-3.49.1.0.jar LoginFrame
   ```

## Features

- User authentication and management
- Dashboard with inventory overview
- Stock tracking and management
- Supplier and hospital management
- Search functionality
- Database integration with SQLite

## Troubleshooting

1. **Class Not Found Error**

   - Ensure the SQLite JDBC driver is in the same directory
   - Verify the classpath includes the JDBC driver

2. **Database Connection Issues**

   - Check if `ppe_inventory.db` exists in the project directory
   - Verify database permissions

3. **Compilation Errors**
   - Ensure JDK 17 or later is installed
   - Check for any missing dependencies

## Support

For any issues or questions, please contact the project maintainers.

## License

[Specify your license here]

## Accounts

1|admin|admin123
2|user1|pass123
3|test|test123
