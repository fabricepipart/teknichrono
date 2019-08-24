curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
chmod +x ./kubectl
sudo mv ./kubectl /usr/local/bin/kubectl
mkdir ${HOME}/.kube
cp src/main/travis/kubeconfig.yaml ${HOME}/.kube/config
sed -i 's/KUBE_CLUSTER_CERTIFICATE/'"$KUBE_CLUSTER_CERTIFICATE"'/g' ${HOME}/.kube/config
sed -i 's/KUBE_ENDPOINT/'"$KUBE_ENDPOINT"'/g' ${HOME}/.kube/config
sed -i 's/KUBE_PORT/'"$KUBE_PORT"'/g' ${HOME}/.kube/config
sed -i 's/KUBE_ADMIN_PWD/'"$KUBE_ADMIN_PWD"'/g' ${HOME}/.kube/config
sed -i 's/KUBE_USERNAME/'"$KUBE_USERNAME"'/g' ${HOME}/.kube/config