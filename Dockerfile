FROM public.ecr.aws/amazoncorretto/amazoncorretto:17-al2-jdk
COPY target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
