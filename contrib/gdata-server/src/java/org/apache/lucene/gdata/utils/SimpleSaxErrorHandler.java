begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ErrorHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXParseException
import|;
end_import

begin_comment
comment|/**  * For use with Digester to throw exceptions on errors  * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|SimpleSaxErrorHandler
specifier|public
specifier|final
class|class
name|SimpleSaxErrorHandler
implements|implements
name|ErrorHandler
block|{
comment|/**      * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)      */
DECL|method|warning
specifier|public
name|void
name|warning
parameter_list|(
name|SAXParseException
name|arg0
parameter_list|)
throws|throws
name|SAXException
block|{
comment|//
block|}
comment|/**      * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)      */
DECL|method|error
specifier|public
name|void
name|error
parameter_list|(
name|SAXParseException
name|arg0
parameter_list|)
throws|throws
name|SAXException
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"ERROR: Can not parse XML Document -- "
operator|+
name|arg0
operator|.
name|getMessage
argument_list|()
argument_list|,
name|arg0
argument_list|)
throw|;
block|}
comment|/**      * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)      */
DECL|method|fatalError
specifier|public
name|void
name|fatalError
parameter_list|(
name|SAXParseException
name|arg0
parameter_list|)
throws|throws
name|SAXException
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"FATAL ERROR: Can not parse XML Document -- "
operator|+
name|arg0
operator|.
name|getMessage
argument_list|()
argument_list|,
name|arg0
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

