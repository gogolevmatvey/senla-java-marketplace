SET DB_NAME=placingadssystem
SET USER=postgres
SET HOST=localhost
SET PORT=5432
SET PGPASSWORD=1111

psql -U %USER% -h %HOST% -p %PORT% -c "DROP DATABASE IF EXISTS %DB_NAME%;"
psql -U %USER% -h %HOST% -p %PORT% -c "CREATE DATABASE PlacingAdsSystem;"
psql -U %USER% -h %HOST% -p %PORT% -d %DB_NAME% -f create_tables.sql

pause

