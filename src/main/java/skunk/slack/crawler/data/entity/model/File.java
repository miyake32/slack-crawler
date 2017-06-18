package skunk.slack.crawler.data.entity.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
//use deprecated annotation for the compatibility with development env. 
@lombok.experimental.Builder
@AllArgsConstructor
public class File implements Serializable {
	private static final long serialVersionUID = -3295090064184153720L;

	public File() {
	}
	
	@Id
	private String id;
	
	@Column(nullable = false)
	private Timestamp created;
	
	@Column(nullable = false)
	private Timestamp timeStamp;
	
	@Column(nullable = false)
	private String name;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private User user;
	
	@Column(nullable = false)
	private String url;
	
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Channel> channels;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		File other = (File) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}
