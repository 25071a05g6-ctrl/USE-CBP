# Smart College Circular Information Extraction System

A desktop application built using **Java Swing** and **MySQL (JDBC)** to process college circular text, extract key event attributes using Regular Expressions, and store/retrieve records.

---

## Folder Contents

- **`Main.java`**: Complete all-in-one Java Swing GUI, Regex information extraction, and JDBC database operations.
- **`DBConnection.java`**: Separate database connection helper (if preferred).
- **`schema.sql`**: Database and table creation script for MySQL.
- **`mysql-connector-j-8.3.0.jar`**: MySQL JDBC Type-4 driver.
- **`run.bat`**: Double-click Windows batch script to compile and run the application.

---

## Quick Start (How to Run)

### 1. Database Setup
Make sure your MySQL server is running (e.g., via XAMPP or MySQL Service), then execute `schema.sql`:
```cmd
mysql -u root -p < schema.sql
```

### 2. Run the Application
Simply double-click:
```text
run.bat
```

Or run via Command Prompt / PowerShell:
```cmd
javac -cp ".;mysql-connector-j-8.3.0.jar" Main.java
java -cp ".;mysql-connector-j-8.3.0.jar" Main
```

---

## Sample Circular for Testing

Paste the following text into the application and click **"Extract Information"**:

```text
Department of CSE is organizing Hackathon 2026 on 20-10-2026 at Seminar Hall. Registration Deadline: 15-10-2026. Eligible students: BTech 1st and 2nd year.
```

**Extracted Attributes:**
- **Department:** `CSE`
- **Event Name:** `Hackathon 2026`
- **Event Date:** `20-10-2026`
- **Registration Deadline:** `15-10-2026`
- **Venue:** `Seminar Hall`
- **Target Students:** `BTech 1st and 2nd year`
