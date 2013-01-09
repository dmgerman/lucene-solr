begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SegmentReadState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SegmentWriteState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NamedSPILoader
import|;
end_import

begin_class
DECL|class|SimpleDocValuesFormat
specifier|public
specifier|abstract
class|class
name|SimpleDocValuesFormat
implements|implements
name|NamedSPILoader
operator|.
name|NamedSPI
block|{
DECL|field|loader
specifier|private
specifier|static
specifier|final
name|NamedSPILoader
argument_list|<
name|SimpleDocValuesFormat
argument_list|>
name|loader
init|=
operator|new
name|NamedSPILoader
argument_list|<
name|SimpleDocValuesFormat
argument_list|>
argument_list|(
name|SimpleDocValuesFormat
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Unique name that's used to retrieve this format when    *  reading the index.    */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**    * Creates a new docvalues format.    *<p>    * The provided name will be written into the index segment in some configurations    * (such as when using {@code PerFieldDocValuesFormat}): in such configurations,    * for the segment to be read this class should be registered with Java's    * SPI mechanism (registered in META-INF/ of your jar file, etc).    * @param name must be all ascii alphanumeric, and less than 128 characters in length.    */
DECL|method|SimpleDocValuesFormat
specifier|protected
name|SimpleDocValuesFormat
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NamedSPILoader
operator|.
name|checkServiceName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|fieldsConsumer
specifier|public
specifier|abstract
name|SimpleDVConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|fieldsProducer
specifier|public
specifier|abstract
name|SimpleDVProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|getName
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DocValuesFormat(name="
operator|+
name|name
operator|+
literal|")"
return|;
block|}
comment|/** looks up a format by name */
DECL|method|forName
specifier|public
specifier|static
name|SimpleDocValuesFormat
name|forName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You called DocValuesFormat.forName() before all formats could be initialized. "
operator|+
literal|"This likely happens if you call it from a DocValuesFormat's ctor."
argument_list|)
throw|;
block|}
return|return
name|loader
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** returns a list of all available format names */
DECL|method|availableDocValuesFormats
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|availableDocValuesFormats
parameter_list|()
block|{
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You called DocValuesFormat.availableDocValuesFormats() before all formats could be initialized. "
operator|+
literal|"This likely happens if you call it from a DocValuesFormat's ctor."
argument_list|)
throw|;
block|}
return|return
name|loader
operator|.
name|availableServices
argument_list|()
return|;
block|}
comment|/**     * Reloads the DocValues format list from the given {@link ClassLoader}.    * Changes to the docvalues formats are visible after the method ends, all    * iterators ({@link #availableDocValuesFormats()},...) stay consistent.     *     *<p><b>NOTE:</b> Only new docvalues formats are added, existing ones are    * never removed or replaced.    *     *<p><em>This method is expensive and should only be called for discovery    * of new docvalues formats on the given classpath/classloader!</em>    */
DECL|method|reloadDocValuesFormats
specifier|public
specifier|static
name|void
name|reloadDocValuesFormats
parameter_list|(
name|ClassLoader
name|classloader
parameter_list|)
block|{
name|loader
operator|.
name|reload
argument_list|(
name|classloader
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

