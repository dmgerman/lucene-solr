begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on May 24, 2003  */
end_comment

begin_package
DECL|package|com.netwebapps.taglib.search
package|package
name|com
operator|.
name|netwebapps
operator|.
name|taglib
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * @company Network Web Application  * @url http://www.netwebapps.com  * @author Bryan LaPlante   */
end_comment

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|jsp
operator|.
name|tagext
operator|.
name|*
import|;
end_import

begin_class
DECL|class|ColumnTagTei
specifier|public
class|class
name|ColumnTagTei
extends|extends
name|TagExtraInfo
block|{
DECL|method|ColumnTagTei
specifier|public
name|ColumnTagTei
parameter_list|()
block|{ 	}
comment|/* 	 * VariableInfo is provided by the servlet container and allows the 	 * FieldTag class to output it's tag variables to the PageContext at runtime 	 * @see javax.servlet.jsp.tagext.TagExtraInfo#getVariableInfo(javax.servlet.jsp.tagext.TagData) 	 */
DECL|method|getVariableInfo
specifier|public
name|VariableInfo
index|[]
name|getVariableInfo
parameter_list|(
name|TagData
name|tagdata
parameter_list|)
block|{
name|VariableInfo
name|avariableinfo
index|[]
init|=
operator|new
name|VariableInfo
index|[
literal|1
index|]
decl_stmt|;
name|avariableinfo
index|[
literal|0
index|]
operator|=
operator|new
name|VariableInfo
argument_list|(
name|tagdata
operator|.
name|getId
argument_list|()
argument_list|,
literal|"com.netwebapps.taglib.search.ColumnTag"
argument_list|,
literal|true
argument_list|,
name|VariableInfo
operator|.
name|NESTED
argument_list|)
expr_stmt|;
return|return
name|avariableinfo
return|;
block|}
block|}
end_class

end_unit

