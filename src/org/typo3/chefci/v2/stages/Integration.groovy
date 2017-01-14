package org.typo3.chefci.v2.stages

import org.typo3.chefci.helpers.JenkinsGlobalLib

class Integration extends AbstractStage {

    Integration(Object script, String stageName) {
        super(script, stageName)
    }

    @Override
    void execute() {
        script.stage(stageName) {
            testkitchen()
        }
    }

    private def testkitchen(){
        script.node {

            def jenkinsHelper = new JenkinsGlobalLib(script)
            jenkinsHelper.globalLibraryScript('cookbook/.kitchen.docker.yml', '.kitchen.docker.yml')

            int result = 0
            script.wrap([$class: 'AnsiColorBuildWrapper', colorMapName: "XTerm"]) {
                script.withEnv(['KITCHEN_LOCAL_YAML=.kitchen.docker.yml']) {
                    result = script.sh(script: 'kitchen test --destroy always', returnStatus: true)
                }
            }
            if (result != 0) {
                script.echo "kitchen returned non-zero exit status"
//                echo "Archiving test-kitchen logs"
//                archive(includes: ".kitchen/logs/${instanceName}.log")
                script.error("kitchen returned non-zero exit status")
            }
        }
    }

}
