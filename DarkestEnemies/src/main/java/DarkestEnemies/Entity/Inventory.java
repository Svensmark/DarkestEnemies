/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DarkestEnemies.Entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 *
 * @author Asger
 */
@Entity
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private List<Long> potionIds;
    private List<Long> trinketIds;

    public Inventory(List<Long> potionIds, List<Long> trinketIds) {
        this.potionIds = potionIds;
        this.trinketIds = trinketIds;
    }

    public Inventory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getPotionIds() {
        return potionIds;
    }

    public void setPotionIds(List<Long> potionIds) {
        this.potionIds = potionIds;
    }
    
    public List<Long> getTrinketIds() {
        return trinketIds;
    }
    
    public void setTrinketIds(List<Long> trinketIds) {
        this.trinketIds = trinketIds;
    }

}
