--Add EMAIL, MOBILE in JCM
ALTER TABLE JOB_CANDIDATE_MAPPING ADD COLUMN EMAIL VARCHAR (50);
ALTER TABLE JOB_CANDIDATE_MAPPING ADD COLUMN MOBILE VARCHAR (15);
ALTER TABLE JOB_CANDIDATE_MAPPING ADD COLUMN COUNTRY_CODE VARCHAR (5);

UPDATE JOB_CANDIDATE_MAPPING SET EMAIL = concat('sdedhia+',ID,'@gmail.com');
UPDATE JOB_CANDIDATE_MAPPING SET MOBILE = concat('87600',ID,'6785');

ALTER TABLE JOB_CANDIDATE_MAPPING ALTER COLUMN EMAIL SET NOT NULL;

-- rename uuid column in jcm
ALTER TABLE JOB_CANDIDATE_MAPPING
rename column jcm_uuid to chatbot_uuid;

Insert into MASTER_DATA (TYPE, VALUE) values
('industry','Any'),
('industry','Accounting / Finance'),
('industry','Advertising / PR / MR / Events'),
('industry','Agriculture / Dairy'),
('industry','Animation'),
('industry','Architecture / Interior Design'),
('industry','Auto / Auto Ancillary'),
('industry','Aviation / Aerospace Firm'),
('industry','Banking / Financial Services / Broking'),
('industry','BPO / ITES'),
('industry','Brewery / Distillery'),
('industry','Chemicals / PetroChemical / Plastic / Rubber'),
('industry','Construction / Engineering / Cement / Metals'),
('industry','Consumer Durables'),
('industry','Courier / Transportation / Freight'),
('industry','Ceramics /Sanitary ware'),
('industry','Defence / Government'),
('industry','Education / Teaching / Training'),
('industry','Electricals / Switchgears'),
('industry','Export / Import'),
('industry','Facility Management'),
('industry','Fertilizers / Pesticides'),
('industry','FMCG / Foods / Beverage'),
('industry','Food Processing'),
('industry','Fresher / Trainee'),
('industry','Gems & Jewellery'),
('industry','Glass'),
('industry','Heat Ventilation Air Conditioning'),
('industry','Hotels / Restaurants / Airlines / Travel'),
('industry','Industrial Products / Heavy Machinery'),
('industry','Insurance'),
('industry','IT-Software / Software Services'),
('industry','IT-Hardware & Networking'),
('industry','Telecom / ISP'),
('industry','KPO / Research /Analytics'),
('industry','Legal'),
('industry','Media / Dotcom / Entertainment'),
('industry','Internet / Ecommerce'),
('industry','Medical / Healthcare / Hospital'),
('industry','Mining'),
('industry','NGO / Social Services'),
('industry','Office Equipment / Automation'),
('industry','Oil and Gas / Power / Infrastructure / Energy'),
('industry','Paper'),
('industry','Pharma / Biotech / Clinical Research'),
('industry','Printing / Packaging'),
('industry','Publishing'),
('industry','Real Estate / Property'),
('industry','Recruitment'),
('industry','Retail'),
('industry','Security / Law Enforcement'),
('industry','Semiconductors / Electronics'),
('industry','Shipping / Marine'),
('industry','Steel'),
('industry','Strategy /Management Consulting Firms'),
('industry','Textiles / Garments / Accessories'),
('industry','Tyres'),
('industry','Water Treatment / Waste Management'),
('industry','Wellness/Fitness/Sports'),
('industry','Other');

ALTER TABLE COMPANY
ADD COLUMN COMPANY_DESCRIPTION TEXT,
ADD COLUMN WEBSITE VARCHAR(245),
ADD COLUMN LANDLINE VARCHAR(10),
ADD COLUMN INDUSTRY INTEGER REFERENCES MASTER_DATA(ID),
ADD COLUMN LINKEDIN VARCHAR(245),
ADD COLUMN FACEBOOK VARCHAR(245),
ADD COLUMN TWITTER VARCHAR(245),
ADD COLUMN LOGO VARCHAR(245),
ADD COLUMN SUBSCRIPTION VARCHAR(5) NOT NULL DEFAULT 'Lite';

ALTER TABLE USERS
ADD COLUMN INVITATION_MAIL_TIMESTAMP TIMESTAMP NOT NULL DEFAULT localtimestamp,
ADD COLUMN RESET_PASSWORD_FLAG BOOL NOT NULL DEFAULT 'f',
ADD COLUMN RESET_PASSWORD_EMAIL_TIMESTAMP TIMESTAMP;

ALTER TABLE JCM_COMMUNICATION_DETAILS
ADD COLUMN CHAT_INVITE_FLAG BOOL DEFAULT 'f';

alter table users alter column user_uuid drop not null;

CREATE TABLE JCM_PROFILE_SHARING_DETAILS (
    ID UUID PRIMARY KEY DEFAULT uuid_generate_v1(),
    SENDER_ID INTEGER REFERENCES USERS(ID) NOT NULL,
    RECEIVER_NAME varchar(45) NOT NULL,
    RECEIVER_EMAIL varchar(50) NOT NULL,
    JOB_CANDIDATE_MAPPING_ID INTEGER REFERENCES JOB_CANDIDATE_MAPPING(ID) NOT NULL,
    EMAIL_SENT_ON TIMESTAMP DEFAULT NULL,
    HIRING_MANAGER_INTEREST BOOL DEFAULT FALSE,
    HIRING_MANAGER_INTEREST_DATE TIMESTAMP DEFAULT NULL,
    CONSTRAINT UNIQUE_JCM_HIRING_MANAGER UNIQUE (ID, SENDER_ID, RECEIVER_EMAIL)
);