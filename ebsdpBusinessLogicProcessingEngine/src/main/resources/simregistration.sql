create table simregistration(sessionid int, msisdn varchar(20), menulevel smallint, status varchar(20), );
insert into simregistration(sessionid, msisdn, menulevel, status, )
values
(1234,'263733661588',),

create table ussdmenu (menulevel smallint, menuoptions varchar(160));
insert into ussdmenu (menulevel, menuoptions)
values
(0,'Please Select either 1 or 2\n 1. Register SIM \n 2. Exit'),
()

-- create table simregister(
--   msisdn varchar(20),
--   status ENUM('main', 'prompt_firstname','prompt_lastname', 'prompt_idnumber', 'prompt_physicaladdress', 'captured', 'registered'),
--   val varchar(100),
--   entry_date datetime,
--   primary key (msisdn, status),
--   key (msisdn));

create table simregister (
  ms_isdn varchar(20),
  state varchar(30),
  id_number varchar(20),
  firstname varchar(255),
  lastname varchar(255),
  physical_address varchar(255),
  date_created datetime,
  date_last_updated datetime,
  primary key (ms_isdn));

insert into simregister (msisdn, status, val )
values ('263733661588', 'prompt_name');

create table ussd_menu_items (
  menu_id varchar(40),
  menu_text varchar(160),
  error_text varchar(160),
  blank_error_text varchar(160),
  primary key (menu_id)
);

insert into ussd_menu_items(menu_id, menu_text, error_text, blank_error_text)
values
  ( 'main',
    'Please Select either 1 or 2\\n 1. Register SIM \\n 2. Exit',
    'INVALID SELECTION\nPlease Select either 1 or 2\n 1. Register SIM \n2. Exit',
    ''),
  ('prompt_firstname',
    'Please enter your first name',
    'Error, You entered an invalid first name, please re-enter your first name',
    'Error, first name cannot be empty, please re-enter your first name'),
  ('prompt_lastname',
    'Please enter your last name',
    'Error, You entered an last name, please re-enter your last name',
    'Error, last name cannot be empty, please re-enter your last name'),
  ('prompt_idnumber',
    'Please enter your ID Number without spaces or any special characters',
    'Error, You entered an invalid ID Number, please re-enter your ID Number without spaces or any special characters',
    'Error, ID Number cannot be empty, please re-enter your ID Number'),
  ('prompt_physicaladdress',
    'Please enter your full address including city',
    '',
    'Error,address cannot be empty, please re-enter your full address including city'),
  ('registered',
    'Hi #firstname, Thank you for registering your line. It will be active within 1hr, SO GO AHEAD TELL SOMEONE!',
    'Error,processing registration, please contact the call center',
    '');

