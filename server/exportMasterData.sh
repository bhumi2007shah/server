#!/bin/sh
echo " "
date
echo "creating master data sql script"

# Capabilities + Complexities master data to ML #40
# this file created an sql script in apache folder that can be downloaded anywhere using url litmusblox.io/capabilityComplexityMaster.sql
# If you want to add more table add in below script " -t <table_name> "

# export psql format create and insert script to root folder
PGPASSWORD="H#X@g0nL1tmu$" pg_dump -h localhost -p 5432 -U postgres -t public.capability_master -t public.complexity_master -t public.level_master -t public.capability_complexity_mapping -t public.complexity_scenario -t public.level_capability_scenario_mapping -d scoringengine> /home/lbprod/capabilityComplexityMaster.psql

# change directory to /tmp/pg2mysql-master
cd /tmp/pg2mysql-master

# convert exported psql file to mysql
php pg2mysql_cli.php /home/lbprod/capabilityComplexityMaster.psql /home/lbprod/capabilityComplexityMaster.sql

#remove public. from table names so it could run in mysql
sed -i -e 's/public.//g' /home/lbprod/capabilityComplexityMaster.sql

# copy scoringEngineMaster file to apache root directory
sudo cp /home/lbprod/capabilityComplexityMaster.sql /var/www/html/capabilityComplexityMaster.sql

#export candidateChatbotLinks.csv
PGPASSWORD="H#X@g0nL1tmu$" psql -U postgres -h localhost -d litmusblox -A -F"," -c "select 
company.company_name as \"Company Name\", 
job.id as JobId, job.job_title as \"Job Title\",
concat(jcm.candidate_first_name, ' ',jcm.candidate_last_name) as \"Candidate Name\",
jcm.chatbot_status as \"Chatbot Status\",
concat('https://chatbot.litmusblox.io/#/',jcm.chatbot_uuid) as \"Chatbot Link\"
from 
job_candidate_mapping jcm 
inner join 
company on jcm.created_by = company.created_by 
inner join 
job on job.id = jcm.job_id;" > /home/lbprod/serverApplication/FileStore/download/candidateChatbotLinks.csv

#export hrScreeningQuestionResponses.csv
PGPASSWORD="H#X@g0nL1tmu$" psql -U postgres -h localhost -d litmusblox -U postgres -A -F"," -c "select
company.company_name as \"Company Name\",
job.id as \"Job Id\", job.job_title as \"Job Title\",
concat(jcm.candidate_first_name, ' ',jcm.candidate_last_name) as \"Candidate Name\",
jsq.ScreeningQn as \"Screening Qn\", jsq.ScreeningOp as \"Screening Op\",
case
	when csqr.response=jsq.ScreeningOp then 1
end
as \"Candidate Response\"
from
job_candidate_mapping jcm
inner join
company on jcm.created_by = company.created_by
inner join
job on job.id = jcm.job_id
inner join
(
	select jsq.id as jsqId, job_id jsqJobId , question as ScreeningQn, unnest(options) as ScreeningOp from job_screening_questions jsq inner join screening_question msq on jsq.master_screening_question_id = msq.id
	union
	select jsq.id as jsqId, job_id jsqJobId, question as ScreeningQn, unnest(options) as ScreeningOp from job_screening_questions jsq inner join user_screening_question usq on jsq.company_screening_question_id=usq.id
	union
	select jsq.id as jsqId, job_id jsqJobId, question as ScreeningQn, unnest(options) as ScreeningOp from job_screening_questions jsq inner join company_screening_question csq ON csq.id = jsq.company_screening_question_id
)as jsq
on jsq.jsqJobId = jcm.job_id
inner join candidate_screening_question_response csqr
on jsq.jsqId = csqr.job_screening_question_id
order by \"Company Name\", \"Job Id\", \"Candidate Name\", \"Screening Qn\";" > /home/lbprod/serverApplication/FileStore/download/hrScreeningQuestionResponses.csv
