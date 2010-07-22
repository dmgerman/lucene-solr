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
comment|/**  * Parameters used with the QueryElevationComponent  *  **/
end_comment

begin_interface
DECL|interface|QueryElevationParams
specifier|public
interface|interface
name|QueryElevationParams
block|{
DECL|field|ENABLE
name|String
name|ENABLE
init|=
literal|"enableElevation"
decl_stmt|;
DECL|field|EXCLUSIVE
name|String
name|EXCLUSIVE
init|=
literal|"exclusive"
decl_stmt|;
DECL|field|FORCE_ELEVATION
name|String
name|FORCE_ELEVATION
init|=
literal|"forceElevation"
decl_stmt|;
block|}
end_interface

end_unit

