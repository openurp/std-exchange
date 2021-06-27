
alter table edu.exemption_credits set schema std;

select * from edu.exchange_students;

create table std.exemption_applies as
select id,id extern_student_id,transcript_path,audit_state,audit_opinion,credits,exemption_credits,updated_at from edu.exchange_students;

create table base.extern_students as
select id,updated_at,begin_on,end_on,std_id,school_id,major_name,level_id,category_id  from edu.exchange_students;

create table base.extern_schools as select * from edu.exchange_schools;

create table std.exchange_grades as select * from edu.exchange_grades;
create table std.exchange_grades_courses as select * from edu.exchange_grades_courses;
alter table std.exchange_grades add audit_state int4;
update std.exchange_grades a set audit_state = (select b.audit_state from edu.exchange_students b where b.id=a.exchange_student_id);
alter table std.exchange_grades rename exchange_student_id to extern_student_id;
update std.exchange_grades a set updated_at = (select b.updated_at from edu.exchange_students b where b.id=a.extern_student_id) where updated_at is null;
select * from std.exchange_grades where updated_at is null;