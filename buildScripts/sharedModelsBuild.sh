echo "Trying to get authentication token"
aws codeartifact get-authorization-token --domain slickpickbets-backend --domain-owner 591565668473 --query authorizationToken --output text
echo "Did we get the token properly?"
export CODEARTIFACT_TOKEN=`aws codeartifact get-authorization-token --domain slickpickbets-backend --domain-owner 591565668473 --query authorizationToken --output text`
echo $CODEARTIFACT_TOKEN
mvn -s settings.xml clean package
echo "Package build successfully. Deploying version now"
aws codeartifact delete-package-versions --domain slickpickbets-backend --repository SlickPickBets --format maven --domain-owner 591565668473 --namespace com.slickpickbets --package shared-models --versions 1.0.1
echo "Just deleted existing package, now we are set to re-deploy package"
mvn -s settings.xml clean package deploy