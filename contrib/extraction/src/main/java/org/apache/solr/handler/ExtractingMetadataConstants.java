begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

begin_comment
comment|/**  *  *  **/
end_comment

begin_interface
DECL|interface|ExtractingMetadataConstants
specifier|public
interface|interface
name|ExtractingMetadataConstants
block|{
DECL|field|STREAM_NAME
name|String
name|STREAM_NAME
init|=
literal|"stream_name"
decl_stmt|;
DECL|field|STREAM_SOURCE_INFO
name|String
name|STREAM_SOURCE_INFO
init|=
literal|"stream_source_info"
decl_stmt|;
DECL|field|STREAM_SIZE
name|String
name|STREAM_SIZE
init|=
literal|"stream_size"
decl_stmt|;
DECL|field|STREAM_CONTENT_TYPE
name|String
name|STREAM_CONTENT_TYPE
init|=
literal|"stream_content_type"
decl_stmt|;
block|}
end_interface

end_unit

