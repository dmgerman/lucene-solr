begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|com.relevanz.indyo
package|package
name|com
operator|.
name|relevanz
operator|.
name|indyo
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache POI" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A datasource is any source of data (filesystem, database, URL, etc)  * which is indexed by SearchIndexer.  *   * @version $Id$  */
end_comment

begin_interface
DECL|interface|IndexDataSource
specifier|public
interface|interface
name|IndexDataSource
block|{
comment|/**      * Key in the map (located in the list returned by getData)      * to represent the class name of the object being indexed.      */
DECL|field|OBJECT_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|OBJECT_CLASS
init|=
literal|"objectClass"
decl_stmt|;
comment|/**      * Key in the map (located in the list returned by getData)      * to represent the uuid of the object being indexed.      */
DECL|field|OBJECT_IDENTIFIER
specifier|public
specifier|static
specifier|final
name|String
name|OBJECT_IDENTIFIER
init|=
literal|"objectId"
decl_stmt|;
comment|/**      * The key in the map (located in the list returned by getData)      * to represent nested datasources.      */
DECL|field|NESTED_DATASOURCE
specifier|public
specifier|static
specifier|final
name|String
name|NESTED_DATASOURCE
init|=
literal|"nestedDataSource"
decl_stmt|;
comment|/**      * Key in the map (located in the list returned by getData)      * to represent the id of the datasource's container. Applies to      * nested datasources.      */
DECL|field|CONTAINER_IDENTIFIER
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_IDENTIFIER
init|=
literal|"containerId"
decl_stmt|;
comment|/**      * Key in the map to represent the class name of the Search Result      * object for this datasource (if any).      */
DECL|field|SEARCH_RESULT_CLASSNAME
specifier|public
specifier|static
specifier|final
name|String
name|SEARCH_RESULT_CLASSNAME
init|=
literal|"resultClassname"
decl_stmt|;
comment|/**      * Retrieve a array of Maps. Each map represents the      * a document to be indexed. The key:value pair of the map      * is the metadata of the document.      */
DECL|method|getData
specifier|public
name|Map
index|[]
name|getData
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

