from java.io import FileInputStream
import os
import shutil

def deploy_war(weblogic_home, admin_url, admin_username, admin_password, target_server, war_file):
    connect(admin_username, admin_password, admin_url)

    app_path = weblogic_home + '/tmp/' + war_file
    deploy(appName=war_file, path=app_path, targets=target_server)

    progress = getDeploymentProgress(appName=war_file)
    while progress.isRunning():
        progress.printStatus()
        progress.waitForUpdate(3000)

    deployment_state = getDeploymentStatus(appName=war_file)
    if deployment_state != 'STATE_ACTIVE':
        print('Error occurred during WAR-file deployment:', deployment_state)

    disconnect()

propInputStream = FileInputStream("src/main/resources/datasource.properties")
configProps = Properties()
configProps.load(propInputStream)

oracleHome = os.environ.get('MW_HOME')
print(oracleHome)
weblogicHome = oracleHome + '/user_projects/domains/' + configProps.get("domain.name")
print(weblogicHome)
adminUrl = configProps.get("admin.url")
adminUsername = configProps.get("admin.username")
adminPassword = configProps.get("admin.password")
targetServer = configProps.get("datasource.target")
warFile = configProps.get("app.name") + '.war'

shutil.copy2('target/' + warFile, weblogicHome + '/tmp/')
shutil.copy2('src/main/resources/createDataSource.py', weblogicHome)
shutil.copy2('src/main/resources/datasource.properties', weblogicHome)

deploy_war(weblogicHome, adminUrl, adminUsername, adminPassword, targetServer, warFile)
