# 🔐 Role Definitions – CRM System

This CRM system follows a **Role-Based Access Control (RBAC)** model to ensure secure and structured access across different business domains and microservices.

Below are the defined roles and their responsibilities:

---

## 🏆 SUPER_ADMIN

* Has full access across the entire platform.
* Manages system-wide configurations, tenants (companies), and role definitions.
* Responsible for overall system governance.

---

## 🏢 ADMIN

* Manages all operations within a specific organization/company.
* Can create and manage employees, assign roles, and oversee business data.
* Has full access to all modules within their organization.

---

## 💼 SALES_MANAGER

* Oversees sales team activities and performance.
* Can view and manage all leads, opportunities, and sales data for the team.
* Responsible for approvals and reporting.

---

## 🤝 SALES_EXECUTIVE

* Handles day-to-day sales activities.
* Can create and manage leads, opportunities, and customer data.
* Access is restricted to their own assigned records.

---

## 🛠 SUPPORT_MANAGER

* Manages customer support operations.
* Assigns tickets/tasks to support agents and monitors resolution progress.
* Has visibility into all support-related data.

---

## 🎧 SUPPORT_AGENT

* Handles customer issues, tickets, and support tasks.
* Works on assigned cases and updates their status.
* Limited to assigned or owned records.

---

## 📦 INVENTORY_MANAGER

* Manages product catalog and inventory levels.
* Responsible for stock updates, product details, and availability tracking.

---

## 📊 PROJECT_MANAGER

* Oversees project execution and task management.
* Assigns tasks, tracks progress, and ensures timely delivery of projects.

---

## 💰 ACCOUNT_MANAGER

* Handles financial and account-related operations.
* Manages sales orders, transactions, and account records.

---

## 👨‍💻 EMPLOYEE

* Basic system user with limited access.
* Can view and update personal profile and assigned tasks.

---

## ⚙️ SYSTEM

* Internal role used for service-to-service communication.
* Ensures secure interaction between microservices (e.g., authentication, gateway).
* Not assigned to human users.

---

## 🔒 Access Control Model

* Permissions are assigned to roles based on business responsibilities.
* Data access is controlled at multiple levels:

    * **Own Data**
    * **Team Data**
    * **Organization-wide Data**
* Designed to support scalability, security, and clear separation of concerns.

---
