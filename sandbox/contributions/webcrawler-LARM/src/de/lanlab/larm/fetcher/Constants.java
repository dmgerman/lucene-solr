begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title:        LARM Lanlab Retrieval Machine<p>  * Description:<p>  * Copyright:    Copyright (c)<p>  * Company:<p>  * @author  * @version 1.0  */
end_comment

begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package

begin_comment
comment|/**  * contains all global constants used in this package  */
end_comment

begin_class
DECL|class|Constants
specifier|public
class|class
name|Constants
block|{
comment|/**      * user agent string a fetcher task gives to the corresponding server      */
DECL|field|USER_AGENT
specifier|public
specifier|static
specifier|final
name|String
name|USER_AGENT
init|=
literal|"Mozilla/4.06 [en] (WinNT; I)"
decl_stmt|;
comment|/**      * Crawler Identification      */
DECL|field|CRAWLER_AGENT
specifier|public
specifier|static
specifier|final
name|String
name|CRAWLER_AGENT
init|=
literal|"Fetcher/0.95"
decl_stmt|;
comment|/**      * size of the temporary buffer to read web documents in      */
DECL|field|FETCHERTASK_READSIZE
specifier|public
specifier|final
specifier|static
name|int
name|FETCHERTASK_READSIZE
init|=
literal|4096
decl_stmt|;
comment|/**      * don't read more than... bytes      */
DECL|field|FETCHERTASK_MAXFILESIZE
specifier|public
specifier|final
specifier|static
name|int
name|FETCHERTASK_MAXFILESIZE
init|=
literal|2000000
decl_stmt|;
block|}
end_class

end_unit

