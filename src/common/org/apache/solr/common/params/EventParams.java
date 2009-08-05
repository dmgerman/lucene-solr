begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  *  *  **/
end_comment

begin_interface
DECL|interface|EventParams
specifier|public
interface|interface
name|EventParams
block|{
comment|/** Event param for things like newSearcher, firstSearcher**/
DECL|field|EVENT
specifier|public
specifier|static
specifier|final
name|String
name|EVENT
init|=
literal|"event"
decl_stmt|;
DECL|field|NEW_SEARCHER
specifier|public
specifier|static
specifier|final
name|String
name|NEW_SEARCHER
init|=
literal|"newSearcher"
decl_stmt|;
DECL|field|FIRST_SEARCHER
specifier|public
specifier|static
specifier|final
name|String
name|FIRST_SEARCHER
init|=
literal|"firstSearcher"
decl_stmt|;
block|}
end_interface

end_unit

