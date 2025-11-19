// Jenkinsfile: Pipeline thử nghiệm đơn giản
pipeline {
    agent any 

    stages {
        stage('Hello CI/CD') {
            steps {
                echo '*** Bắt đầu quy trình Tích hợp Liên tục ***'
                sh 'echo "Mã nguồn được kéo từ Git và đang chạy trên Jenkins!"'
            }
        }
        stage('Test Success') {
            steps {
                sh 'echo "CI thành công!"'
            }
        }
    }
}
