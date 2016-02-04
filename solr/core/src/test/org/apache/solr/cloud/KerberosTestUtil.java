begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|AppConfigurationEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|minikdc
operator|.
name|MiniKdc
import|;
end_import

begin_class
DECL|class|KerberosTestUtil
specifier|public
class|class
name|KerberosTestUtil
block|{
comment|/**    * Returns a MiniKdc that can be used for creating kerberos principals    * and keytabs.  Caller is responsible for starting/stopping the kdc.    */
DECL|method|getKdc
specifier|public
specifier|static
name|MiniKdc
name|getKdc
parameter_list|(
name|File
name|workDir
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|conf
init|=
name|MiniKdc
operator|.
name|createConf
argument_list|()
decl_stmt|;
return|return
operator|new
name|MiniKdc
argument_list|(
name|conf
argument_list|,
name|workDir
argument_list|)
return|;
block|}
comment|/**    * Programmatic version of a jaas.conf file suitable for connecting    * to a SASL-configured zookeeper.    */
DECL|class|JaasConfiguration
specifier|public
specifier|static
class|class
name|JaasConfiguration
extends|extends
name|Configuration
block|{
DECL|field|clientEntry
specifier|private
specifier|static
name|AppConfigurationEntry
index|[]
name|clientEntry
decl_stmt|;
DECL|field|serverEntry
specifier|private
specifier|static
name|AppConfigurationEntry
index|[]
name|serverEntry
decl_stmt|;
DECL|field|clientAppName
DECL|field|serverAppName
specifier|private
name|String
name|clientAppName
init|=
literal|"Client"
decl_stmt|,
name|serverAppName
init|=
literal|"Server"
decl_stmt|;
comment|/**      * Add an entry to the jaas configuration with the passed in name,      * principal, and keytab. The other necessary options will be set for you.      *      * @param clientPrincipal The principal of the client      * @param clientKeytab The location of the keytab with the clientPrincipal      * @param serverPrincipal The principal of the server      * @param serverKeytab The location of the keytab with the serverPrincipal      */
DECL|method|JaasConfiguration
specifier|public
name|JaasConfiguration
parameter_list|(
name|String
name|clientPrincipal
parameter_list|,
name|File
name|clientKeytab
parameter_list|,
name|String
name|serverPrincipal
parameter_list|,
name|File
name|serverKeytab
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clientOptions
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|clientPrincipal
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"keyTab"
argument_list|,
name|clientKeytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"useKeyTab"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"storeKey"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"useTicketCache"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|clientOptions
operator|.
name|put
argument_list|(
literal|"refreshKrb5Config"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|jaasProp
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.jaas.debug"
argument_list|)
decl_stmt|;
if|if
condition|(
name|jaasProp
operator|!=
literal|null
operator|&&
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|jaasProp
argument_list|)
condition|)
block|{
name|clientOptions
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|clientEntry
operator|=
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|getKrb5LoginModuleName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|clientOptions
argument_list|)
block|}
expr_stmt|;
if|if
condition|(
name|serverPrincipal
operator|!=
literal|null
operator|&&
name|serverKeytab
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|serverOptions
init|=
operator|new
name|HashMap
argument_list|(
name|clientOptions
argument_list|)
decl_stmt|;
name|serverOptions
operator|.
name|put
argument_list|(
literal|"principal"
argument_list|,
name|serverPrincipal
argument_list|)
expr_stmt|;
name|serverOptions
operator|.
name|put
argument_list|(
literal|"keytab"
argument_list|,
name|serverKeytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|serverEntry
operator|=
operator|new
name|AppConfigurationEntry
index|[]
block|{
operator|new
name|AppConfigurationEntry
argument_list|(
name|getKrb5LoginModuleName
argument_list|()
argument_list|,
name|AppConfigurationEntry
operator|.
name|LoginModuleControlFlag
operator|.
name|REQUIRED
argument_list|,
name|serverOptions
argument_list|)
block|}
expr_stmt|;
block|}
block|}
comment|/**      * Add an entry to the jaas configuration with the passed in principal and keytab,       * along with the app name.      *       * @param principal The principal      * @param keytab The keytab containing credentials for the principal      * @param appName The app name of the configuration      */
DECL|method|JaasConfiguration
specifier|public
name|JaasConfiguration
parameter_list|(
name|String
name|principal
parameter_list|,
name|File
name|keytab
parameter_list|,
name|String
name|appName
parameter_list|)
block|{
name|this
argument_list|(
name|principal
argument_list|,
name|keytab
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|clientAppName
operator|=
name|appName
expr_stmt|;
name|serverAppName
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAppConfigurationEntry
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|clientAppName
argument_list|)
condition|)
block|{
return|return
name|clientEntry
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|serverAppName
argument_list|)
condition|)
block|{
return|return
name|serverEntry
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getKrb5LoginModuleName
specifier|private
name|String
name|getKrb5LoginModuleName
parameter_list|()
block|{
name|String
name|krb5LoginModuleName
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"IBM"
argument_list|)
condition|)
block|{
name|krb5LoginModuleName
operator|=
literal|"com.ibm.security.auth.module.Krb5LoginModule"
expr_stmt|;
block|}
else|else
block|{
name|krb5LoginModuleName
operator|=
literal|"com.sun.security.auth.module.Krb5LoginModule"
expr_stmt|;
block|}
return|return
name|krb5LoginModuleName
return|;
block|}
block|}
comment|/**    *  These Locales don't generate dates that are compatibile with Hadoop MiniKdc.    */
DECL|field|brokenLanguagesWithMiniKdc
specifier|private
specifier|final
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|brokenLanguagesWithMiniKdc
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"th"
argument_list|)
operator|.
name|getLanguage
argument_list|()
argument_list|,
operator|new
name|Locale
argument_list|(
literal|"ja"
argument_list|)
operator|.
name|getLanguage
argument_list|()
argument_list|,
operator|new
name|Locale
argument_list|(
literal|"hi"
argument_list|)
operator|.
name|getLanguage
argument_list|()
argument_list|)
decl_stmt|;
comment|/**     *returns the currently set locale, and overrides it with {@link Locale#US} if it's     * currently something MiniKdc can not handle    *    * @see Locale#setDefault    */
DECL|method|overrideLocaleIfNotSpportedByMiniKdc
specifier|public
specifier|static
specifier|final
name|Locale
name|overrideLocaleIfNotSpportedByMiniKdc
parameter_list|()
block|{
name|Locale
name|old
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
if|if
condition|(
name|brokenLanguagesWithMiniKdc
operator|.
name|contains
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
argument_list|)
condition|)
block|{
name|Locale
operator|.
name|setDefault
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
block|}
return|return
name|old
return|;
block|}
block|}
end_class

end_unit

