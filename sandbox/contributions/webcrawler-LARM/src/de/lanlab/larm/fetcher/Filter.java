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
comment|/**  * base class of all filter classes  */
end_comment

begin_class
DECL|class|Filter
specifier|public
specifier|abstract
class|class
name|Filter
block|{
comment|/** 	 * number of items filtered. augmented directly by 	 * the inheriting classes 	 */
DECL|field|filtered
specifier|protected
name|int
name|filtered
init|=
literal|0
decl_stmt|;
DECL|method|getFiltered
specifier|public
name|int
name|getFiltered
parameter_list|()
block|{
return|return
name|filtered
return|;
block|}
block|}
end_class

end_unit

