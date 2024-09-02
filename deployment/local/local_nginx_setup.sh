echo "Registering local domain"
domain="chessgrinder.local.shefer.space"
if grep -Fxq "127.0.0.1 $domain" /etc/hosts
then
    echo "Local domain already registered"
else
  echo "127.0.0.1 $domain" >> /etc/hosts
fi
