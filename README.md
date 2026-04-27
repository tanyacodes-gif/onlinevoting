# 🗳️ Online Voting System

![Java](https://img.shields.io/badge/Language-Java-blue)
![Status](https://img.shields.io/badge/Project-Completed-brightgreen)
![License](https://img.shields.io/badge/License-MIT-yellow)

A secure and user-friendly **Online Voting System** designed to simulate real-world digital elections.  
This project ensures **one vote per user**, maintains **vote integrity**, and provides **accurate results**.

---

## 📌 Features

- 🔐 Voter Authentication (only registered users can vote)  
- 🧾 One Vote per User (prevents duplicate voting)  
- 📊 Real-Time Vote Counting  
- 👨‍💼 Admin Panel to manage candidates and view results  
- ⚠️ Exception Handling for invalid inputs  

---

## 🛠️ Tech Stack

**Frontend:** HTML, CSS, JavaScript  
**Backend:** Java (Core Java / Servlets / JSP)  
**Database:** MySQL / File Handling  

**Concepts Used:**
- Object-Oriented Programming (OOP)  
- Collections (HashSet, ArrayList)  
- Exception Handling  
- Authentication Logic  

---

## 📂 Project Structure
OnlineVotingSystem/
│── src/
│ ├── models/
│ │ ├── User.java
│ │ ├── Candidate.java
│ │
│ ├── services/
│ │ ├── VotingService.java
│ │ ├── AuthService.java
│ │
│ ├── exceptions/
│ │ ├── InvalidUserException.java
│ │ ├── DuplicateVoteException.java
│ │
│ ├── main/
│ ├── Main.java
│
│── database/
│ ├── schema.sql
│
│── README.md

---

## 🚀 How It Works

1. User registers or logs in  
2. System verifies credentials  
3. User selects a candidate  
4. Vote is recorded securely  
5. Duplicate voting is prevented  
6. Admin can view final results  

---

## ▶️ How to Run

### 🖥️ Java (Console Version)

```bash
git clone https://github.com/your-username/online-voting-system.git
Open in IntelliJ / Eclipse
Run Main.java
