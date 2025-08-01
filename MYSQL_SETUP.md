# MySQL Database Setup

## Prerequisites
1. MySQL Server 8.0+ o'rnatilgan bo'lishi kerak
2. MySQL Workbench yoki boshqa MySQL client (ixtiyoriy)

## Database yaratish

### 1. MySQL ga ulanish
```bash
mysql -u root -p
```

### 2. Database yaratish
```sql
-- Development database
CREATE DATABASE auth_db_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Production database
CREATE DATABASE auth_db_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Main database
CREATE DATABASE auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. User yaratish (ixtiyoriy, xavfsizlik uchun)
```sql
-- Auth service uchun maxsus user yaratish
CREATE USER 'auth_user'@'localhost' IDENTIFIED BY 'strong_password_here';

-- Database permissions berish
GRANT ALL PRIVILEGES ON auth_db.* TO 'auth_user'@'localhost';
GRANT ALL PRIVILEGES ON auth_db_dev.* TO 'auth_user'@'localhost';
GRANT ALL PRIVILEGES ON auth_db_prod.* TO 'auth_user'@'localhost';

FLUSH PRIVILEGES;
```

## Konfiguratsiya

### Development environment
```bash
java -jar -Dspring.profiles.active=dev build/libs/auth-0.0.1-SNAPSHOT.jar
```

### Production environment
```bash
# Environment variables o'rnatish
export DB_USERNAME=auth_user
export DB_PASSWORD=strong_password_here

java -jar -Dspring.profiles.active=prod build/libs/auth-0.0.1-SNAPSHOT.jar
```

## Connection Testing

### 1. MySQL connection test
```bash
mysql -u root -p -e "SELECT 1"
```

### 2. Application connection test
Application ishga tushgandan keyin:
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health check: http://localhost:8080/actuator/health (agar actuator qo'shilgan bo'lsa)

## Troubleshooting

### Common Issues:

1. **Connection refused**
   - MySQL service ishlab turganini tekshiring
   - Port 3306 ochiq ekanini tekshiring

2. **Authentication failed**
   - Username/password to'g'riligini tekshiring
   - MySQL user permissions ni tekshiring

3. **Database not found**
   - Database yaratilganini tekshiring
   - Database nomi to'g'riligini tekshiring

4. **Timezone issues**
   - MySQL da timezone o'rnatilganini tekshiring
   - `SET GLOBAL time_zone = '+00:00';` buyrug'ini bajaring
