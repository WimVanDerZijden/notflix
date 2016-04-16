# Introduction

This project is the result of two consecutive university assignments. The first iteration was for the course Web Technology at Saxion, creating a Server-Client application using the JAX-RS implementation Jersey and client side technologies like jQuery, jQuery Mobile and AngularJS. A website was created that allows users to browse and rate movies.

The second iteration was for the course "Data Science, topic 2: Semantic Web" at the University of Twente. For this assignment the website was enriched using semantic web technologies. The JSON datastore was replaced with RDF triples, which were then linked to open linked data using the DBpedia sparql endpoint at www.dbpedia.org/sparql. The enhanced website is just a proof of concept of this enrichment, showing movie title, abstract, directors, writers and actors in several different languages such as English, German, French, Japanese, Chinese, Arab and Russian.

# Getting Started

1. Install and run Eclipse IDE for Java EE developers (http://www.eclipse.org), version Luna or higher

2. Download and unzip Apache Tomcat 8.0 from https://tomcat.apache.org

2. Copy this repository's URL to your clipboard

3. In Eclipse, do File -> Import -> Git -> Projects from Git -> Clone URI. Paste the repository's URL into the URI field. Click next a couple times, accepting all the defaults.

4. Right click the project: Run As -> Run on Server. Choose Manually define a new server. Choose Apache Tomcat v8.0. Specify the installation directory as the directory where you unzipped Tomcat 8. Click next a couple times, accepting the defaults.

5. If the web app is started successfully, the log should say: WebApp: Web App started

6. Use a browser to navigate to http://localhost:8080/notflix
