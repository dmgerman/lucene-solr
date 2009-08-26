begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|Remote
import|;
end_import

begin_comment
comment|/**  * Marker interface to enable subclasses of {@link org.apache.lucene.search.Searchable} to be used via  * Java RMI. Classes implementing this interface can be used as a RMI -  * "remote object".  *<p>  * {@link RMIRemoteSearchable} extends {@link org.apache.lucene.search.Searchable} and can transparently  * be used as a such.  *<p>  * Example usage:  *   *<pre>  *   RMIRemoteSearchable remoteObject = ...;  *   String remoteObjectName = ...;  *   Naming.rebind (remoteObjectName, remoteObject);  *   Searchable luceneSearchable = (Searchable) Naming.lookup (remoteObjectName);  *</pre>  *   *</p>  *</p>  *   *  @version $Id:$  */
end_comment

begin_interface
DECL|interface|RMIRemoteSearchable
specifier|public
interface|interface
name|RMIRemoteSearchable
extends|extends
name|Searchable
extends|,
name|Remote
block|{  }
end_interface

end_unit

