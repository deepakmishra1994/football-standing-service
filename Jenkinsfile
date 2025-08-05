pipeline {
    agent any

    environment {
        // Docker configuration
        DOCKER_IMAGE = 'football-standing-service'
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_REGISTRY = 'your-registry.com'

        // Kubernetes configuration
        KUBE_NAMESPACE = 'football-service'

        // Credentials
        API_KEY = credentials('api-football-key')
        DOCKER_CREDS = credentials('docker-registry-credentials')
        KUBE_CONFIG = credentials('kubernetes-config')

        // Quality gates
        SONAR_PROJECT_KEY = 'football-standing-service'
        COVERAGE_THRESHOLD = '80'
    }

    options {
        // Keep only last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))

        // Timeout for entire pipeline
        timeout(time: 30, unit: 'MINUTES')

        // Skip default checkout
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout') {
            steps {
                // Clean workspace and checkout code
                cleanWs()
                checkout scm

                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                }
            }
        }

        stage('Environment Setup') {
            steps {
                script {
                    // Print environment information
                    sh '''
                        echo "=== Environment Information ==="
                        echo "Java Version: $(java -version)"
                        echo "Maven Version: $(mvn -version)"
                        echo "Docker Version: $(docker --version)"
                        echo "Git Commit: ${GIT_COMMIT_SHORT}"
                        echo "Build Number: ${BUILD_NUMBER}"
                    '''
                }
            }
        }

        stage('Code Quality & Security') {
            parallel {
                stage('Static Code Analysis') {
                    steps {
                        script {
                            // SonarQube analysis
                            withSonarQubeEnv('SonarQube') {
                                sh '''
                                    mvn clean compile sonar:sonar \
                                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                                        -Dsonar.java.coveragePlugin=jacoco \
                                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                                '''
                            }
                        }
                    }
                }

                stage('Dependency Security Scan') {
                    steps {
                        script {
                            // OWASP Dependency Check
                            sh '''
                                mvn org.owasp:dependency-check-maven:check \
                                    -DfailBuildOnCVSS=7 \
                                    -Dformat=XML
                            '''
                        }
                    }
                    post {
                        always {
                            publishHTML([
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'target',
                                reportFiles: 'dependency-check-report.html',
                                reportName: 'OWASP Dependency Check Report'
                            ])
                        }
                    }
                }
            }
        }

        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh '''
                            mvn clean test \
                                -Dspring.profiles.active=test \
                                -Djacoco.skip=false
                        '''
                    }
                    post {
                        always {
                            // Publish test results
                            publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'

                            // Publish code coverage
                            publishCoverage adapters: [
                                jacocoAdapter('target/site/jacoco/jacoco.xml')
                            ]

                            // Quality gate check
                            script {
                                def coverage = sh(
                                    script: "grep -o 'missed=\"[0-9]*\"' target/site/jacoco/jacoco.xml | head -1 | grep -o '[0-9]*'",
                                    returnStdout: true
                                ).trim()

                                if (coverage && coverage.toInteger() > (100 - COVERAGE_THRESHOLD.toInteger())) {
                                    error("Code coverage ${100 - coverage.toInteger()}% is below threshold ${COVERAGE_THRESHOLD}%")
                                }
                            }
                        }
                    }
                }

                stage('Integration Tests') {
                    steps {
                        sh '''
                            mvn failsafe:integration-test failsafe:verify \
                                -Dspring.profiles.active=test \
                                -DskipUnitTests=true
                        '''
                    }
                    post {
                        always {
                            publishTestResults testResultsPattern: 'target/failsafe-reports/*.xml'
                        }
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh '''
                        mvn clean package -DskipTests \
                            -Dspring.profiles.active=prod \
                            -Dbuild.number=${BUILD_NUMBER} \
                            -Dgit.commit=${GIT_COMMIT_SHORT}
                    '''

                    // Archive artifacts
                    archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: false
                }
            }
        }

        stage('Docker Build & Security Scan') {
            steps {
                script {
                    // Build Docker image
                    def dockerImage = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")

                    // Tag as latest
                    dockerImage.tag("latest")

                    // Container security scan with Trivy
                    sh """
                        docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
                            -v \$(pwd):/tmp/.cache/ \
                            aquasec/trivy:latest image \
                            --exit-code 1 \
                            --severity HIGH,CRITICAL \
                            --format json \
                            --output /tmp/.cache/trivy-report.json \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
            post {
                always {
                    // Archive security scan results
                    archiveArtifacts artifacts: 'trivy-report.json', allowEmptyArchive: true
                }
            }
        }

        stage('Docker Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                        def image = docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}")
                        image.push()
                        image.push("latest")

                        // Clean up local images
                        sh "docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest || true"
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    // Deploy to Kubernetes staging environment
                    withKubeConfig([credentialsId: 'kubernetes-config']) {
                        sh """
                            # Update deployment image
                            kubectl set image deployment/football-service \
                                football-service=${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG} \
                                --namespace=staging

                            # Wait for rollout to complete
                            kubectl rollout status deployment/football-service \
                                --namespace=staging \
                                --timeout=300s
                        """
                    }
                }
            }
        }

        stage('Smoke Tests') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    // Run smoke tests against staging environment
                    sh '''
                        # Wait for service to be ready
                        for i in {1..30}; do
                            if curl -f http://staging.football-service.com/actuator/health; then
                                echo "Service is healthy"
                                break
                            fi
                            echo "Waiting for service to be ready..."
                            sleep 10
                        done

                        # Run smoke tests
                        mvn test -Dtest=SmokeTests \
                            -Dtest.environment=staging \
                            -Dtest.base.url=http://staging.football-service.com
                    '''
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                script {
                    // Manual approval for production deployment
                    timeout(time: 10, unit: 'MINUTES') {
                        input message: 'Deploy to production?',
                              ok: 'Deploy',
                              submitterParameter: 'APPROVER'
                    }

                    // Deploy to production
                    withKubeConfig([credentialsId: 'kubernetes-config']) {
                        sh """
                            # Blue-green deployment strategy
                            kubectl patch deployment football-service \
                                -p '{"spec":{"template":{"spec":{"containers":[{"name":"football-service","image":"${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}"}]}}}}' \
                                --namespace=production

                            # Wait for rollout
                            kubectl rollout status deployment/football-service \
                                --namespace=production \
                                --timeout=600s
                        """
                    }

                    // Tag release in Git
                    sh """
                        git tag -a v${BUILD_NUMBER} -m "Release version ${BUILD_NUMBER}"
                        git push origin v${BUILD_NUMBER}
                    """
                }
            }
        }
    }