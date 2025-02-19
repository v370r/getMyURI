# getmyuri

## 🚀 Overview
getmyuri is a **URL shortening and website routing platform** designed to provide **structured, secure, and trackable links** for individuals, businesses, and developers. Unlike traditional URL shorteners like Bit.ly or TinyURL, getmyuri offers:

- **Custom URL Trees** (e.g., `getmyuri.com/username/project/docs`)
- **Password-protected links**
- **Expiration and time-limited links**
- **Click analytics and tracking**
- **Website hosting and routing** (users can map their public IP/domain to a structured URL)A
- **API support** for developers to integrate getmyuri with their applications

---

## 🌟 Key Features
### **1️⃣ URL Shortening & Custom Links**
- Users can create **short and structured URLs** for their projects, campaigns, or knowledge bases.
- Example:
  ```
  getmyuri.com/username/linuxadmins/notes
  getmyuri.com/influencername/summerBonanza/shoes
  ```
- **Password protection** for restricted access.
- **Expiration settings** to automatically disable links after a defined period.
- **Analytics & click tracking** in the user dashboard.

### 2️⃣ Resume Support **
- Users can create their own urls like **getmyuri.com/vijay/resume** to send over the internet by attaching their updated Url of theeir drive or anythin

### **2️⃣ Website Hosting & Routing**
- Users can **route traffic to their own hosted websites/projects**.
- Example:
  ```
  getmyuri.com/ownername/mywebsite → Routes to User's Public IP
  ```
- Supports **single-page applications (React, Angular, Vue)**.
- Ideal for **freelancers, developers, and startups** that need a quick way to share projects.

### **3️⃣ API for Developers & Businesses**
- REST API for programmatic **link creation, analytics, and management**.
- Can be integrated into **marketing tools, campaign trackers, and SaaS applications**.

### **4️⃣ Multi-Cloud Infrastructure & CI/CD**
- **Oracle Cloud** (24GB) for production hosting.
- **MacOS Mini (8GB + 2TB HDD)** for staging/testing.
- **AWS Free Tier (t3.micro)** for CI/CD automation.
- Uses **Jenkins, SonarQube, Docker, Terraform, Helm, and ArgoCD**.

---

## 🛠 Tech Stack
### **Backend:**
- Spring Boot (REST API, Authentication, URL Processing)
- PostgreSQL / MySQL (Database for URL storage & analytics)
- Redis (Caching for fast redirects)
- Spring Security + JWT (Authentication & Authorization)

### **Frontend:**
- Angular (User dashboard, analytics, and link creation UI)
- Material UI / Tailwind CSS (Modern and responsive design)
- Chart.js / D3.js (Click analytics visualization)

### **CI/CD & DevOps:**
- Jenkins + SonarQube (CI, Testing, Security Analysis)
- Docker + Kubernetes (Containerized deployment)
- Terraform + Helm (Infrastructure as Code)
- ArgoCD (Automated deployments)
- Prometheus + Grafana (Monitoring & Performance Metrics)
- ELK Stack (Logging & Debugging)

---

## 📌 Deployment Architecture
1️⃣ **Oracle Cloud (Production Server)**  
   - Hosts **Spring Boot Backend & Angular Frontend**
   - Runs the main **API & Database**
   
2️⃣ **MacOS Mini (Staging & Testing Server)**  
   - For **pre-production testing**
   - Stores **logs & analytics** in the **2TB HDD**

3️⃣ **AWS Free Tier (CI/CD & Monitoring Servers)**  
   - **Jenkins & SonarQube** (Automated testing & security scanning)
   - **Terraform & Helm** (Infrastructure setup & updates)
   - **ArgoCD & Kubernetes** (Automated deployment)
   - **Monitoring with Prometheus & Grafana**

---

## 📌 Development Roadmap
### **📍 Sprint 1: Core Backend Development**
✅ Set up **Spring Boot API for URL Shortening**  
✅ Implement **Database Schema**  
✅ Build **Authentication & User Management**

### **📍 Sprint 2: Frontend & User Dashboard**
✅ Develop **Angular Dashboard**  
✅ Integrate **Frontend-Backend API**  
✅ Implement **Click Analytics & Reports**

### **📍 Sprint 3: CI/CD & Automation**
✅ Set up **Jenkins + SonarQube + Docker Builds**  
✅ Deploy **ArgoCD for GitOps**  
✅ Automate **Terraform + Helm Deployments**

### **📍 Sprint 4: Website Routing & Hosting**
✅ Implement **Public IP/Domain Routing**  
✅ Configure **Nginx Proxy for SPA Support**  
✅ Launch **Beta Version**

---

## 🏗 Installation & Setup
### **1️⃣ Clone the repository**
```sh
 git clone https://github.com/yourusername/getmyuri.git
 cd getmyuri
```

### **2️⃣ Backend Setup**
```sh
 cd backend
 mvn clean install
 java -jar target/getmyuri.jar
```

### **3️⃣ Frontend Setup**
```sh
 cd frontend
 npm install
 ng serve
```

### **4️⃣ Docker Deployment**
```sh
 docker-compose up -d
```

---

## 🤝 Contributing
We welcome contributions! 🚀 To contribute:
1. **Fork** the repository.
2. **Create** a new branch: `feature-xyz`
3. **Commit** your changes.
4. **Submit** a Pull Request (PR).

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🎯 Contact & Support
For questions or support, reach out to:
📧 Email: `support@getmyuri.com`
📌 GitHub Issues: [Create an Issue](https://github.com/yourusername/getmyuri/issues)
🚀 Follow on Twitter: `@getmyuri`

---

🚀 **getmyuri - Simplifying URLs, Hosting, and Routing!** 🔥

