plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.x' // O la versión que estés usando
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.psicodeli'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17' // O la versión de Java que uses (ej. 17 o 21)
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Core & Web
    implementation 'org.springframework.boot:springframework-boot-starter-web'
    
    // Base de datos y JPA (que ya lo tienes estructurado)
    implementation 'org.springframework.boot:springframework-boot-starter-data-jpa'
    runtimeOnly 'com.mysql:mysql-connector-j' // O el driver de tu base de datos (PostgreSQL, H2, etc.)

    // Seguridad y JWT
    implementation 'org.springframework.boot:springframework-boot-starter-security'
    
<<<<<<< Updated upstream
    // PostgreSQL JDBC driver
    runtimeOnly("org.postgresql:postgresql")

    // SQLite JDBC driver
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    // Hibernate SQLite Dialect for Spring Boot 3+ (Hibernate 6)
    implementation("org.hibernate.orm:hibernate-community-dialects:6.4.4.Final")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
=======
    // Librerías JJWT para la creación y validación de tokens
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'

    // Lombok para ahorrar código repetitivo (getters, setters, constructors)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Pruebas
    testImplementation 'org.springframework.boot:springframework-boot-starter-test'
>>>>>>> Stashed changes
}

test {
    usePlatformName = 'junit-platform'
}
