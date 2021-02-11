pipeline {
    agent any

    tools {
        maven 'Maven 3.6.3'
    }

    stages {

        stage('Package') {
            steps {
                sh 'mvn package -Dmaven.test.skip=true'
            }
        }

        stage('Deliver for Feature CF') {
            steps {
                pushToCloudFoundry(
                    target: "${param.CF_TARGET}",
                    organization: "${param.CF_ORGANIZATION}",
                    cloudSpace: "${param.CF_CLOUDSPACE}",
                    credentialsId: "${param.CF_CREDENTIALS_ID}",
                    selfSigned: 'true',
                    manifestChoice: [
                        value: 'jenkinsConfig',
                        name: "${params.name}",
                        instances: '1',
                        buildPack: "${params.buildpack}",
                        memory: "${params.memory}",
                        timeout: "${params.timeout}",
                        appPath: '${params.path}',
                        envVars:[
                            [key: 'TZ', value: "${params.TZ}"],
                            [key: 'ELASTICSEARCH_HOST', value: "${params.ELASTICSEARCH_HOST}"],
                            [key: 'ELASTICSEARCH_PROTOCOL', value: "${params.ELASTICSEARCH_PROTOCOL}"],
                            [key: 'ELASTICSEARCH_PORT', value: "${params.ELASTICSEARCH_PORT}"],
                            [key: 'ELASTICSEARCH_USER', value: "${params.ELASTICSEARCH_USER}"],
                            [key: 'ELASTICSEARCH_SCT', value: "${params.ELASTICSEARCH_SCT}"],
                            [key: 'ELASTICSEARCH_INDEX', value: "${params.ELASTICSEARCH_INDEX}"],
                            [key: 'ERROR_INCLUDE_MESSAGE', value: "${params.ERROR_INCLUDE_MESSAGE}"],
                            [key: 'TIME_ZONE', value: "${params.TIME_ZONE}"],
                        ]
                    ]
                )
            }
        }
    }
}