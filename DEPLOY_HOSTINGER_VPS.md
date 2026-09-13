# Hostinger VPS Deployment Guide for MyTuition (PocketBase)

This guide documents the exact steps to deploy PocketBase on a Hostinger VPS with HTTPS, persistence, auto-healing, and automatic backups.

---

## 1. Connect & Initial Setup

```bash
# 1. SSH into VPS
ssh root@<YOUR_VPS_IP>

# 2. Create a dedicated application user
adduser mytuition
usermod -aG sudo mytuition

# 3. Download PocketBase (v0.23+)
su - mytuition
mkdir -p ~/mytuition && cd ~/mytuition
wget https://github.com/pocketbase/pocketbase/releases/download/v0.23.0/pocketbase_0.23.0_linux_amd64.zip
unzip pocketbase_0.23.0_linux_amd64.zip
chmod +x pocketbase
```

---

## 2. Copy Hook Files

Copy the `pb_hooks/` folder from this repository into `/home/mytuition/mytuition/pb_hooks/`:
```bash
# Inside /home/mytuition/mytuition:
# Ensure pb_hooks/ contains:
#   - mytuition_routes.js
#   - mytuition_cron.js
#   - mytuition_record_hooks.js
#   - seed.js
```

---

## 3. Systemd Service (24/7 Keep-Alive)

Create `/etc/systemd/system/pocketbase.service`:

```ini
[Unit]
Description=MyTuition PocketBase Server
After=network.target

[Service]
Type=simple
User=mytuition
Group=mytuition
LimitNOFILE=4096
WorkingDirectory=/home/mytuition/mytuition
ExecStart=/home/mytuition/mytuition/pocketbase serve --http="127.0.0.1:8090"
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable pocketbase
sudo systemctl start pocketbase
sudo systemctl status pocketbase
```

---

## 4. Reverse Proxy with Caddy (Automatic HTTPS)

Install Caddy:
```bash
sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https curl
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt update
sudo apt install caddy -y
```

Configure `/etc/caddy/Caddyfile`:
```caddy
api.techtomorrow.in {
    reverse_proxy 127.0.0.1:8090
}
```

Restart Caddy:
```bash
sudo systemctl restart caddy
```

---

## 5. First-Time Setup & Seeding

1. Open `https://api.techtomorrow.in/_/` in your browser.
2. Create the superuser (Admin Email + Password).
3. Call the seed endpoint to initialize demo data:
```bash
curl -X POST https://api.techtomorrow.in/api/mytuition/seed-demo
```
