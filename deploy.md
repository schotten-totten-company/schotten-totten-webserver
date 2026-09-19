# Deploy procedure on an Ubuntu server

## Open ports
```
sudo ufw allow 22
sudo ufw allow 8057
sudo iptables -I INPUT -p tcp -m tcp --dport 8057 -j ACCEPT
sudo ufw enable
sudo ufw reload
```

## Edit crontab
```
sudo crontab -e
```

## with the following 2 lines (don't forget to update server X.X version) :
```
@reboot java -Dserver.port=8057 -jar /home/ubuntu/schotten-totten-webserver-X.X.jar &
0 4 * * * sudo apt update -y && sudo apt upgrade -y && sudo reboot`
```