for i in {8521..8450}  # This will download 60 since rfcs skip numbers sometimes
do
    wget -q https://www.rfc-editor.org/rfc/rfc$i.txt
done
