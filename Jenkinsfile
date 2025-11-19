pipeline {
    agent any
    
    stages {
        stage('Hello') {
            steps {
                echo 'Bắt đầu quy trình Tích hợp Liên tục'
                // Khuyến nghị đặt lệnh chạy Linux trong sh ''
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
