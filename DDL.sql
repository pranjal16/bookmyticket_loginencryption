SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Admin](
	[adminID] [int] IDENTITY(1,1) NOT NULL,
	[flightCode] [varchar](45) NOT NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
ALTER TABLE [dbo].[Admin] ADD  CONSTRAINT [PK_Admin] PRIMARY KEY CLUSTERED 
(
	[adminID] ASC,
	[flightCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO



SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[member](
	[memberID] [int] IDENTITY(1,1) NOT NULL,
	[lname] [varchar](45) NULL,
	[fname] [varchar](45) NULL,
	[address] [varchar](45) NULL,
	[zip] [varchar](6) NULL,
	[state] [varchar](45) NULL,
	[username] [varchar](45) NULL,
	[password] [varchar](45) NULL,
	[email] [varchar](45) NULL,
	[ssn] [varchar](20) NULL,
	[securityQuestion] [varchar](45) NULL,
	[securityAnswer] [varchar](45) NULL,
	[adminID] [int] NULL,
	[passwordsalt] [nvarchar](256) NULL,
	[passwordhash] [nvarchar](256) NULL
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[member] ADD PRIMARY KEY CLUSTERED 
(
	[memberID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [fk_member_Admin1_idx] ON [dbo].[member]
(
	[adminID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create trigger [dbo].[UpdateLoginEncryption] on [dbo].[member]
after INSERT
as
BEGIN
      declare @uid UNIQUEIDENTIFIER;
      select @uid=NEWID();
      
      UPDATE member
      SET PASSwordsalt=@uid
      from inserted i
      WHERE i.MemberID= member.memberID

end
GO
ALTER TABLE [dbo].[member] ENABLE TRIGGER [UpdateLoginEncryption]
GO



SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[flight](
	[flightCode] [varchar](45) NOT NULL,
	[destination] [varchar](45) NULL,
	[departureTime] [datetime] NULL,
	[arrivalTime] [datetime] NULL,
	[seatCapacity] [varchar](5) NULL,
	[adminID] [int] NULL,
	[bookingID] [int] NULL,
	[FlightID] [int] IDENTITY(1,1) NOT NULL,
	[departureCity] [nvarchar](45) NULL
) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
ALTER TABLE [dbo].[flight] ADD PRIMARY KEY CLUSTERED 
(
	[flightCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [fk_flight_Admin1_idx] ON [dbo].[flight]
(
	[adminID] ASC,
	[bookingID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create trigger [dbo].[tgr_Add_Admin_Flight] on [dbo].[flight] after insert
AS
BEGIN


set IDENTITY_INSERT ADMIN ON


insert into Admin(adminID,flightcode)
select i.adminID,i.flightcode
from inserted i
where i.adminID is not NULL
      and not exists 
      (select *
from  Admin a
where a.flightCode=i.flightCode
      and a.adminID=i.adminID) 

set IDENTITY_INSERT ADMIN OFF




end
GO
ALTER TABLE [dbo].[flight] ENABLE TRIGGER [tgr_Add_Admin_Flight]
GO



SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[booking](
	[bookingID] [int] IDENTITY(1,1) NOT NULL,
	[bookingDate] [date] NULL,
	[memberID] [int] NULL,
	[flightCode] [varchar](45) NULL,
	[FlightID] [int] NULL
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[booking] ADD PRIMARY KEY CLUSTERED 
(
	[bookingID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[booking] ADD  DEFAULT (NULL) FOR [bookingDate]
GO
ALTER TABLE [dbo].[booking] ADD  DEFAULT (NULL) FOR [memberID]
GO
ALTER TABLE [dbo].[booking] ADD  DEFAULT (NULL) FOR [flightCode]
GO
ALTER TABLE [dbo].[booking]  WITH CHECK ADD  CONSTRAINT [fk_memberID] FOREIGN KEY([memberID])
REFERENCES [dbo].[member] ([memberID])
GO
ALTER TABLE [dbo].[booking] CHECK CONSTRAINT [fk_memberID]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create trigger [dbo].[addbooking] on [dbo].[booking]
after INSERT
AS
BEGIN

         update booking
         set bookingDate=getdate(),flightCode=f.flightCode
         from flight f,inserted i 
         where booking.FlightID=f.FlightID
               and booking.bookingDate is NULL
               and booking.flightCode is NULL


end
GO
ALTER TABLE [dbo].[booking] ENABLE TRIGGER [addbooking]
GO



/****Routine calls*****/

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[test](@MemberID INT)
-- Get a list of tables and views in the current database
as
begin
insert into Admin (flightcode) values ('new')


update member
set adminID=(select top 1 adminID from dbo.Admin where flightcode='new')
where Memberid=@MemberID

end
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create PROCEDURE [dbo].[UpdateAdminFlightCode] 
@flightID INT
AS
BEGIN

    declare @adminID INT
    SELECT top 1 @adminID=adminID FROM dbo.Admin where flightCode='NEW';

    update Admin
    set flightCode=(SELECT TOP 1 flightCode from flight where flightID=@flightID)
    where adminID=@adminID

end
GO
