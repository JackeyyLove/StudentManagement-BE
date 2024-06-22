#!/bin/bash

#CHECK IMAGE STAGE
echo -e "Đây là bước kiểm tra Dockerfile\n"
echo "Nhập đường dẫn tới Dockerfile"
read df_path
Dockerfile=$df_path/Dockerfile

if [ ! -f "$Dockerfile" ]; then
  echo "Dockerfile not found: $Dockerfile"
  exit 1
fi

echo -e "Nội dung của Dockerfile: \n$(cat $Dockerfile)\n"

declare -i warn=0
declare -i check=0

# Kiem tra USER root
if grep -q '^USER root' "$Dockerfile"; then
  warn+=1	
  echo -e "\nCảnh báo $warn: Bạn đã khai báo USER root, chú ý khai báo lại một user có ít đặc quyền hơn để tránh bị tấn công leo thang đặc quyền"
fi

# Kiem tra su dung USER 
if grep -q '^USER ' "$Dockerfile"; then
  check+=1
  echo -e "\nCheck $check: Khai báo USER đã được sử dụng"
else 
  warn+=1
  echo -e "\nCảnh báo $warn: Chưa sử dụng khai báo USER, bạn nên sử dụng USER thay vì dùng root vì rất dễ bị tấn công leo thang đặc quyền"
fi
if grep -q '^EXPOSE' "$Dockerfile"; then
  if grep -q '^EXPOSE 22' "$Dockerfile"; then
    warn+=1
    echo "Cảnh báo $warn: Bạn đang EXPOSE cổng 22, chú ý không thực expose cổng này vì chúng ta không nên cho người khác SSH vào container ."
  else
    check+=1
    echo "Check $check: Đã sử dụng EXPOSE và không vi phạm."
  fi
else
  warn+=1
  echo "Cảnh báo $warn: Bạn chưa sử dụng EXPOSE, nên chỉ định các port  mà container sẽ sử dụng thay vì expose ra toàn bộ cổng của container."
fi

# Kiem tra tag
if grep -q 'FROM .*:latest' "$Dockerfile"; then
  warn+=1
  echo "Cảnh báo $warn: Bạn đang sử dụng tag 'latest', cần tránh sử dụng 'latest' vì chúng không tồn tại rủi ro chưa được kiểm chứng ."
elif ! grep -q 'FROM .*:[^ ]' "$Dockerfile"; then
  warn+=1
  echo "Cảnh báo $warn: Bạn chưa chỉ định tag cụ thể cho base image. Hãy sử dụng một tag cụ thể."
else
  check+=1
  echo "Check $check: Đã chỉ định tag cụ thể cho base image."
fi
# Kiem tra cai dat goi theo quy dinh 
if grep -q 'apt-get install' "$Dockerfile"; then
  if ! grep -q 'apt-get update && apt-get install -y --no-install-recommends' "$Dockerfile"; then
    warn+=1
    echo -e "\nCảnh báo $warn: Hãy sử dụng 'apt-get update && apt-get install -y --no-install-recommends' để cài đặt gói."
  else
    check+=1
    echo -e "\nCheck $check: Sử dụng cách thức chuẩn để cài đặt gói với apt-get."
  fi
fi

# Kiem tra ADD 
if grep -q '^ADD ' "$Dockerfile"; then
  warn+=1
  echo -e "\nCảnh báo $warn: Bạn đang sử dụng 'ADD', hãy sử dụng 'COPY' thay thế."
else
  check+=1
  echo -e "\nCheck $check: Đã sử dụng 'COPY' thay vì 'ADD'."
fi

# Kiem tra  multistage build
if grep -q 'FROM .* as ' "$Dockerfile" || grep -q 'FROM .* AS ' "$Dockerfile"; then
  check+=1
  echo -e "\nCheck $check: Đã sử dụng multistage build."
else
  warn+=1
  echo -e "\nCảnh báo $warn: Bạn chưa sử dụng multistage build, hãy xem xét sử dụng multistage build để giảm kích thước hình ảnh Docker."
fi

# Kiem tra  sudo 
if grep -q 'sudo ' "$Dockerfile"; then
  warn+=1
  echo -e "\nCảnh báo $warn: Không nên sử dụng 'sudo' trong Dockerfile."
else 
  check+=1
  echo -e "\nCheck $check: Dockerfile không chứa câu lệnh sudo"
fi

# Kiem tra lenh curl 
if grep -q 'curl .* | sh' "$Dockerfile" || grep -q 'curl .* | bash' "$Dockerfile"; then
  warn+=1
  echo -e "\nCảnh báo $warn: Tránh sử dụng 'curl' với 'sh' hoặc 'bash' để tránh rủi ro bảo mật."
else
  check+=1
  echo -e "\nCheck $check: Dockerfile không sử dụng câu lệnh curl nên có thể giảm rủi ro bị tấn công MITM"
fi

echo -e "\nKiểm tra Dockerfile hoàn tất. Tổng số cảnh báo: $warn. Tổng số kiểm tra thành công: $check."
if [[ "$warn" -eq 0 ]]; then 
	# BUILD IMAGE STAGE
	echo -e "-----------------------------------------\n"
	echo -e "Đây là bước build image\n"
	echo -e "Nhập tên của image\n"
	read image_name
	echo -e "Nhập tag của image\n"
	read image_tag
	docker build -t "$image_name:$image_tag" "$df_path"
	if [ $? -eq 0 ]; then
  		echo -e "Build image thành công"
	else
  		echo -e "Build image thất bại"
  		exit 1

	fi
else 
	echo -e "Kiểm tra và viết lại Dockerfile trước khi build image\n"
	exit 1
fi 
# Scan with Trivy
trivy image "$image_name:$image_tag"
# RUN CONTAINER STAGE
prompt() {
    local var_name=$1
    local prompt_text=$2
    local default_value=$3

    read -p "$prompt_text [$default_value]: " input
    eval $var_name="${input:-$default_value}"
}
# Prompt for memory limit
prompt MEMORY "Nhap gioi han memory(e.g., 512m)" "512m"

# Prompt for CPU limit
prompt CPUS "Nhap so luong CPU cap phat (e.g., 0.5)" "0.5"

# Prompt for CPU shares
prompt CPU_SHARES "Nhap so luong CPU co the chia se (relative weight, e.g., 256)" "256"

# Prompt for PID limit
prompt PIDS_LIMIT "Nhap so luong tien trinh gioi han (e.g., 100)" "100"

# Prompt for user ID
prompt USER_ID "Nhap user id su dung cho lenh run nay(e.g., 1000)" "1000"

# Prompt for group ID
prompt GROUP_ID "Nhap group id su dung cho lenh run nay (e.g., 1000)" "1000"

prompt PORT_HOST "Nhap cong mapping cua host(e.g., 8080)" "8080"

prompt PORT_CONTAINER "Nhap cong mapping cua container(e.g., 8080)" "8080"

prompt NETWORK "Nhap ten network su dung (e.g., spring-mysql-net)" "midterm_spring-mysql-net"
# Construct the Docker run command
DOCKER_RUN_COMMAND="docker run \
  --cap-drop ALL \
  --cap-add=CHOWN \
  --memory $MEMORY \
  --memory-swap $MEMORY \
  --cpu-shares $CPU_SHARES \
  --cpus $CPUS \
  --network $NETWORK \
  --pids-limit $PIDS_LIMIT \
  -p $PORT_HOST:$PORT_CONTAINER -d\
  --security-opt no-new-privileges \
  --tmpfs /run:rw,noexec,nosuid \
  --tmpfs /tmp:rw,noexec,nosuid \
  $image_name:$image_tag"

# Display the constructed command
echo "Lenh run container nhu sau: "
echo "$DOCKER_RUN_COMMAND"

# Option to run the constructed command
read -p "Dong y chay docker run mac dinh? (y/N): " run_now
if [[ $run_now =~ ^[Yy]$ ]]; then
    eval $DOCKER_RUN_COMMAND
else
    echo "Ban lua chon chay thu cong cau lenh docker run, co the anh huong rui ro ve an toan bao mat neu chay khong dung."
fi
