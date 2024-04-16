pipeline {
  agent any
  stages {
    stage('SCM') {
      steps {
        git(
          url: 'https://samraghull@bitbucket.org/coherentdev/d2d-backend.git',
          credentialsId: 'D2D',
          branch: "dev"
        )
      }
    }
    stage('Build') {
      steps {
        sh 'cd ./D2D && mvn clean install'
        
      }
    }
    stage('Port cleaning') {
      steps {
        sh 'kill -9 $(lsof -t -i:8050) || true'
      }
    }
    stage('Execution') {
      steps {
        sh 'JENKINS_NODE_COOKIE=dontKillMe nohup java -jar D2D/target/D2D-0.0.1-SNAPSHOT.jar &'
      }
    }
  }
}