begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.regexp
package|package
name|org
operator|.
name|apache
operator|.
name|regexp
package|;
end_package

begin_comment
comment|/**  * This class exists as a gateway to access useful Jakarta Regexp package protected data.  */
end_comment

begin_class
DECL|class|RegexpTunnel
specifier|public
class|class
name|RegexpTunnel
block|{
DECL|method|getPrefix
specifier|public
specifier|static
name|char
index|[]
name|getPrefix
parameter_list|(
name|RE
name|regexp
parameter_list|)
block|{
name|REProgram
name|program
init|=
name|regexp
operator|.
name|getProgram
argument_list|()
decl_stmt|;
return|return
name|program
operator|.
name|prefix
return|;
block|}
block|}
end_class

end_unit

