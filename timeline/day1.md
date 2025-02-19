# DAY1: 
## **Date:** `2024-01-26`
# **🚀 Multi-Project Server Setup on Mac Mini (Ubuntu 22) with Nginx & Spring Boot**

## **📌 Overview**

This guide documents the complete setup for hosting multiple projects on a **Mac Mini running Ubuntu 22**, using **Nginx as a web server and reverse proxy** to manage traffic to different Spring Boot applications.

---

## **📂 Directory Structure**

```bash
/home/ubuntu/projects/
│── getmyuri
│   ├── backend  # Spring Boot Backend
│   ├── frontend # Angular Frontend
│   ├── nginx-config
│── project2
│   ├── backend
│   ├── frontend
│── project3
│   ├── backend
│   ├── frontend
```

---

## **🔹 1. Purchased & Configured Domain (**\`\`**)**

| **Step**                 | **Action**                                       |
| ------------------------ | ------------------------------------------------ |
| **Bought domain**        | Purchased `getmyuri.com` on Namecheap            |
| **Updated DNS settings** | Added A and CNAME records in Namecheap           |
| **Verified DNS**         | Ran `nslookup getmyuri.com` to check propagation |
| **Get Public IP**        | curl -4 ifconfig.me                              |

### **✅ Namecheap DNS Configuration**

| Record Type  | Host  | Value          |
| ------------ | ----- | -------------- |
| A Record     | `@`   | `67.176.36.203`|
| CNAME Record | `www` | `@`            |

### **❌ Issue: Namecheap required Fully Qualified Domain Name (FQDN)**

✅ **Fix:** Used `getmyuri.com.` *(trailing dot to force FQDN)*

---

## **🔹 2. Installed & Configured Nginx**

### **✅ Steps Completed**

```bash
sudo apt update
sudo apt install nginx -y
```

- Created a test page in `/var/www/getmyuri/index.html`
- Configured Nginx virtual host:

```bash
sudo nano /etc/nginx/sites-available/getmyuri
```

**Nginx Config:**

```nginx
server {
    listen 80;
    server_name getmyuri.com www.getmyuri.com;
    root /var/www/getmyuri;
    index index.html;
}
```

- Enabled the site:

```bash
sudo ln -s /etc/nginx/sites-available/getmyuri /etc/nginx/sites-enabled/
sudo systemctl restart nginx
```

### **❌ Issue: Couldn’t Access Website from the Internet**

✅ **Fix:**

```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

- Enabled **port forwarding in router**.
- Verified open ports using: [https://www.yougetsignal.com/tools/open-ports/](https://www.yougetsignal.com/tools/open-ports/)

---

## **🔹 3. Installed SSL (HTTPS) with Let’s Encrypt**

### **✅ Steps Completed**

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d getmyuri.com -d www.getmyuri.com
```

- Enabled **automatic SSL renewal**:

```bash
sudo crontab -e
```

**Added:**

```bash
0 3 * * * certbot renew --quiet
```

- Restarted Nginx:

```bash
sudo systemctl restart nginx
```

### **❌ Issue: Certbot Asked for FQDN**

✅ **Fix:** Waited for DNS propagation before running Certbot again.

---

## \*\*🔹 4. Set Up Spring Boot Backend for \*\*\`\`

### **✅ Steps Completed**

- Installed Java & Maven:

```bash
sudo apt install openjdk-17-jdk maven -y
```

- Created a Spring Boot project:

```bash
mvn archetype:generate \
  -DgroupId=com.getmyuri \
  -DartifactId=getmyuri-backend \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
```

- Added API endpoint:

```java
@RestController
@RequestMapping("/api")
public class HomeController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Spring Boot Backend!";
    }
}
```

- Set backend port in `application.properties`:

```properties
server.port=8080
```

- Built and ran the app:

```bash
mvn clean install
mvn spring-boot:run
```

- Verified API at `http://localhost:8080/api/hello`

### **❌ Issue: API Not Accessible from Domain**

✅ **Fix:** Configured Nginx as a reverse proxy:

```nginx
location /api/ {
    proxy_pass http://localhost:8080/;
    proxy_set_header Host $host;
}
```

- Restarted Nginx:

```bash
sudo systemctl restart nginx
```

---

## **🔹 5. Structured Server for Multiple Projects**

- Assigned different ports for each backend:
  - GetMyURI → `8080`
  - Project2 → `8081`
  - Project3 → `8082`
- Updated **Nginx config** for each project.

---

## **🔹 6. Automated Backend Startup with systemd**

- Created systemd service for GetMyURI backend:

```bash
sudo nano /etc/systemd/system/getmyuri.service
```

**Service config:**

```ini
[Unit]
Description=Spring Boot Backend for GetMyURI
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/projects/getmyuri/backend
ExecStart=/usr/bin/java -jar target/getmyuri.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

- Enabled and started the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable getmyuri
sudo systemctl start getmyuri
```

---

## **🚀 Current Setup Summary**

| Project      | URL                    | Backend API                      | Status    |
| ------------ | ---------------------- | -------------------------------- | --------- |
| **GetMyURI** | `https://getmyuri.com` | `https://getmyuri.com/api/hello` | ✅ Running |
| **Project2** | `https://project2.com` | `https://project2.com/api/hello` | ✅ Running |
| **Project3** | `https://project3.com` | `https://project3.com/api/hello` | ✅ Running |

---

## **🔹 Issues Faced & Fixes**

| **Issue**                         | **Fix**                                       |
| --------------------------------- | --------------------------------------------- |
| Namecheap required FQDN           | Used `getmyuri.com.` (trailing dot)           |
| Couldn’t access site externally   | Enabled **port forwarding & UFW rules**       |
| Certbot SSL issue                 | Waited for **DNS propagation** before running |
| Spring Boot API not reachable     | Configured **Nginx reverse proxy**            |
| Backend process stopped on reboot | Used **systemd service**                      |

---

## **📌 Next Steps**

✅ **Integrate a database (PostgreSQL) for URL storage** ✅ **Develop frontend (React/Next.js) for user interaction** ✅ **Implement authentication & URL shortening logic**

🎉 **Server is fully configured & ready for deployment!** 🚀

